/*
    FEITO POR JULIANO LEONARDO SOARES
*/


#include "simulator.h"
#include <stdio.h>
#include <iostream>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <getopt.h>
#include <ctype.h>

using namespace std;

int A_application = 0;
int A_transport   = 0;
int B_application = 0;
int B_transport   = 0;
float time_local  = 0;

/*Definir variáveis específicas para o protocolo*/

/* Variáveis básicas pertencentes ao SR */
float incrementoTemporizador;
int tamanhoJanela;
int slidingWindow;
int senderBuffer   = 20000;
int receiverBuffer = 20000;
int baseA;
int baseB;
int buscaA;
int buscaB;
bool pacoteRepetido;
float tempoDecorrido;

/*variáveis para informações extras de estatísticas*/
int denominador;
int at;
int ackWithInvalidSeqNum;
int pacoteCorrompido;
int ACKcorrompido;
int ackCount;

/*tipos de struct extras usados*/
struct msg storeLastMsg;
struct pkt storeLastAck;
struct pkt ackFromB;


struct packInfoA
{
    struct pkt pacote;
    bool partOfBuffer;
    float sendingTime;
    bool pushedtToL3;
    bool pushedtToL5;
};

struct packInfoB
{
    struct pkt pacote;
    bool partOfBuffer;
};

/* struct de alocacao de memoria e array*/
struct packInfoA *ptrA= (struct packInfoA*)malloc(senderBuffer*sizeof(struct packInfoA));
struct packInfoB *ptrB= (struct packInfoB*)malloc(receiverBuffer*sizeof(struct packInfoB));

/*****************************************************************************/

int   win_size;
int   TRACE    = 1;      
int   nsim     = 0;   /* número de mensagens de 5 a 4 até agora */
int   nsimmax  = 0;   /* número de mensagens a gerar */
float lossprob;       /* probabilidade de que um pacote seja descartado */
float corruptprob;    /* probabilidade de que um bit é pacote é invertido */
float lambda;         /* taxa de chegada de mensagens da camada 5 */
int   ntolayer3;      /* número enviado para a camada 3 */
int   nlost;          /* número perdido na media */
int   ncorrupt;       /* número corrompido pela media*/

/****************************************************************************/

/* possiveis erros: */
#define  TIMER_INTERRUPT 0
#define  FROM_LAYER5     1
#define  FROM_LAYER3     2

#define  OFF   0
#define  ON    1
#define   A    0
#define   B    1


struct event {
    float evtime;           /* evento de tempo */
    int evtype;             /* evento tipo de codigo */
    int eventity;           /* onde o evento ocorre */
    struct pkt *pktptr;     /* ponteiro para o pacote do evento */
    struct event *prev;
    struct event *next;
};
struct event *evlist = NULL;   /* lista de eventos ocorridos */


int calculaPayloadChecksum(struct pkt packPayload)          
{

    int payloadChecksum = 0;
    int i=0;
    while(i<(sizeof(packPayload.payload)))
    {
        payloadChecksum=payloadChecksum+packPayload.payload[i];
        i++;
    }
    return payloadChecksum;
}

int calculaChecksum(struct pkt pack)                        
{
    int initChecksum=0;
    initChecksum = (pack.acknum+pack.seqnum+calculaPayloadChecksum(pack))*2;
    return initChecksum;
}

/*various setters*/
int setToZero(int a)
{
    a=0;
    return a;
}

bool setToFalse(bool a)
{
    a=false;
    return a;
}

bool setToTrue(bool a)
{
    a=true;
    return a;
}

/*varias verificacoes*/

int isCorrupt(int a, int b)                                //checks if the pacote is corrupt on the basis of two checksum values
{
    if(a==b)
    {
        return 1;   //pacote não está corrompido
    }
    else
    {
        return 0;   //pacote está corrompido
    }
}

float calculaPerformace(int a, int b)
{
    return (float) a/b;
}

void TimeoutAdaptativo()
{
    float eff=calculaPerformace(ackCount, denominador);
    if(eff>=1.0)
    {
        incrementoTemporizador=12.0;
        printf("selecionado - 12\n");
        return;
    }
    if(eff>0.6 && eff<1.0)
    {
        incrementoTemporizador=11.0;
        printf("selecionado - 11\n");
        return;
    }
    if(eff>0.30 && eff<.60)
    {
        incrementoTemporizador=10.0;
        printf("selecionado - 10\n");
        return;
    }
    if(eff<0.3)
    {
        incrementoTemporizador=9.0;
        printf("selecionado - 9\n");
        return;
    }
    else
    {
        return;
    }
}

void storeAndpacketize(int seek, struct pkt pacote, struct msg mensagem)
{
    strncpy(pacote.payload,mensagem.data,sizeof(pacote.payload));
    pacote.checksum=calculaChecksum(pacote);
    ptrA[seek].pushedtToL3=setToFalse(ptrA[seek].pushedtToL3);
    ptrA[seek].partOfBuffer=setToTrue(ptrA[seek].partOfBuffer);
    ptrA[seek].pushedtToL5=setToFalse(ptrA[seek].pushedtToL5);
    ptrA[seek].pacote=pacote;

}


/* chamado da camada 5, passou os dados para serem enviados para o outro lado */
void A_output(struct msg mensagem)
{
    printf("-------------------------------%d------------------------------\n",buscaA);
    denominador++;
    pkt pacote;
    setToZero(pacote.checksum);
    setToZero(pacote.acknum);
    pacote.seqnum=buscaA;
    storeAndpacketize(buscaA, pacote, mensagem);
    slidingWindow=baseA+tamanhoJanela;

    if(buscaA>slidingWindow)
    {
        // simplesmente atualize o buscador para que novos dados recebidos sejam empacotados e armazenados na nova posição do buscador
        buscaA++;
    }
    else
    {
        int y=baseA;
        while(y<slidingWindow)
        {
           if(ptrA[y].partOfBuffer==true)
            {
                if(ptrA[y].pushedtToL3==false && ptrA[y].pushedtToL5==false)
                {
                    ptrA[y].sendingTime=get_sim_time(); // cada pacote tem seu próprio horário de envio. Isso será usado para monitorar a perda do pacote.
                    //envia pacote
                    tolayer3(0, ptrA[y].pacote);
                    ptrA[y].pushedtToL3=setToTrue(ptrA[y].pushedtToL3);
                    at++;
                    buscaA++;
                    if(at%5==0)
                    {
                        TimeoutAdaptativo();
                    }

                }
            }
            y++;
        }

    }
}


void A_input(struct pkt pacote)
{
    int payloadChecksum=0;
    int verifyChecksum=0;
    verifyChecksum=pacote.acknum+pacote.seqnum;

    int i=0;
    while(i<(sizeof(pacote.payload)))
    {
        payloadChecksum=payloadChecksum+pacote.payload[i];
        i++;
    }
    verifyChecksum=(verifyChecksum+payloadChecksum)*2;
    int Bom_Ruim = isCorrupt(pacote.checksum, verifyChecksum);
    slidingWindow=baseA+tamanhoJanela;

    if(Bom_Ruim==1)
    {
        if(pacote.acknum>=baseA)
        {
            if(pacote.acknum<slidingWindow)
            {
                ackCount++;
                ptrA[pacote.acknum].pushedtToL5=setToTrue(ptrA[pacote.acknum].pushedtToL5);
                
                int y = baseA;
                while(y<=slidingWindow)
                {
                    if(ptrA[y].pushedtToL5==false)
                    {
                        break;
                    }
                    else
                    {
                        baseA++;
                    }
                    y++;
                }

                
                int g=baseA;
                while(g<slidingWindow)
                {
                    if(ptrA[g].partOfBuffer==true)
                    {
                        if(ptrA[g].pushedtToL3==false && ptrA[g].pushedtToL5==false)
                        {
                            ptrA[g].sendingTime=get_sim_time();
                            tolayer3(0, ptrA[g].pacote);
                            ptrA[g].pushedtToL3=setToTrue(ptrA[g].pushedtToL3);

                            if(at%5==0)
                            {
                                TimeoutAdaptativo();
                            }

                        }
                    }
                    g++; 
                }

            }
        }

    }
    else
    {
        if(Bom_Ruim==0)
        {
            ACKcorrompido++;
        }
        else
        {
            ackWithInvalidSeqNum++;
        }
    }
}

/* chamado quando o temporizador de A termina */
void A_timerinterrupt()
{
    /* compara o tempo decorrido desde que cada pacote foi enviado com o tempo limite */
    int y=baseA;
    slidingWindow=baseA+tamanhoJanela;
    while(y<slidingWindow)
    {
        if(ptrA[y].pushedtToL3==true)
        {
            if(ptrA[y].pushedtToL5==false)
            {
                float currentTime=get_sim_time();
                tempoDecorrido=currentTime-ptrA[y].sendingTime;
                if (tempoDecorrido>=incrementoTemporizador)
                {
                    ptrA[y].sendingTime=get_sim_time();
                    tolayer3(0, ptrA[y].pacote);
                    if(at%5==0)
                    {
                        TimeoutAdaptativo();
                    }

                }
            }
        }
        y++;
    }
    starttimer(0,1.0);
}

void A_init()
{
    tamanhoJanela=getwinsize();
    //printf("window size selected is: %d\n", tamanhoJanela);
    incrementoTemporizador=12.0;
    baseA=1;
    buscaA=1;
    ACKcorrompido=0;
    ackWithInvalidSeqNum=0;
    pacoteCorrompido=0;
    starttimer(0,1.0);
    denominador=0;
    at=0;

}

void B_input(struct pkt pacote)
{
    printf("--------------------B_input---------------------\n");

    int verifyChecksum=0;
    int payloadChecksum=0;
    verifyChecksum=pacote.seqnum+pacote.acknum;

    int i=0;
    while(i<(sizeof(pacote.payload)))
    {
        payloadChecksum=payloadChecksum+pacote.payload[i];
        i++;
    }
    verifyChecksum=(verifyChecksum+payloadChecksum)*2;
    int Bom_Ruim=isCorrupt(pacote.checksum,verifyChecksum);
    if(Bom_Ruim==0)
    {
        pacoteCorrompido++;
        return;
    }

    pacoteRepetido=false;

    if(ptrB[pacote.seqnum].partOfBuffer==true)
    {
        if(ptrB[pacote.seqnum].pacote.seqnum==pacote.seqnum)
        {
            pacoteRepetido=setToTrue(pacoteRepetido);
        }
    }

    slidingWindow=baseB+tamanhoJanela;
    if(Bom_Ruim==1)
    {
        if(pacoteRepetido==false)
        {
            if(pacote.seqnum>=baseB)
            {
                if(pacote.seqnum<slidingWindow)
                {
                    setToZero(ackFromB.checksum);
                    ackFromB.acknum=pacote.seqnum;
                    ackFromB.seqnum=pacote.seqnum;
                    strncpy(ackFromB.payload, "confirmação",sizeof(ackFromB.payload));
                    ackFromB.checksum=calculaChecksum(ackFromB);
                    tolayer3(1,ackFromB);
                    buscaB=pacote.seqnum;

                    ptrB[buscaB].pacote=pacote;
                    ptrB[buscaB].partOfBuffer=setToTrue(ptrB[buscaB].partOfBuffer);

                    int g=baseB;
                    while(g<slidingWindow)
                    {
                        if(ptrB[g].partOfBuffer==true)
                        {
                            tolayer5(1,ptrB[g].pacote.payload);
                            baseB++;
                        }
                        else
                        {
                            break;
                        }
                        g++; 
                    }

                }
            }
        }
    }

    if(Bom_Ruim==1 && pacoteRepetido==true)
    {
        setToZero(ackFromB.checksum);
        ackFromB.acknum=pacote.seqnum;
        ackFromB.seqnum=pacote.seqnum;
        strncpy(ackFromB.payload, "confirmação",sizeof(ackFromB.payload));
        ackFromB.checksum=calculaChecksum(ackFromB);
        tolayer3(1,ackFromB);
        return;
    }
    

}

void B_init()
{
    baseB=1;
    buscaB=0;
}

// serve para gerar numero aleatorio
float numeroaleatorio()
{
    double mmm = 2147483647;   
    float x;                   
    x = rand()/mmm;            
    return(x);
}


void insereEvento(struct event *p)
{
    struct event *q,*qold;

    if (TRACE>2) {
        printf("            INSERIR EVENTO: O tempo e %lf\n",time_local);
        printf("            INSERIR EVENTO: O proximo sera %lf\n",p->evtime);
    }
    q = evlist;     
    if (q==NULL) {  
        evlist=p;
        p->next=NULL;
        p->prev=NULL;
    }
    else {
        for (qold = q; q !=NULL && p->evtime > q->evtime; q=q->next)
            qold=q;
        if (q==NULL) {   
            qold->next = p;
            p->prev = qold;
            p->next = NULL;
        }
        else if (q==evlist) { 
            p->next=evlist;
            p->prev=NULL;
            p->next->prev=p;
            evlist = p;
        }
        else {     
            p->next=q;
            p->prev=q->prev;
            q->prev->next=p;
            q->prev=p;
        }
    }
}


void generate_next_arrival()
{
    double x,log(),ceil();
    struct event *evptr;
    //    //char *malloc();
    float ttime;
    int tempint;

    if (TRACE>2)
        printf("          GERAR PRÓXIMA CHEGADA: criar nova chegada\n");

    x = lambda*numeroaleatorio()*2;  
    

    evptr = (struct event *)malloc(sizeof(struct event));
    evptr->evtime =  time_local + x;
    evptr->evtype =  FROM_LAYER5;
    if (BIDIRECTIONAL && (numeroaleatorio()>0.5) )
        evptr->eventity = B;
    else
        evptr->eventity = A;
    insereEvento(evptr);
}



/* Inicia o SIMULADOR */

void init(int seed)                         
{
    int i;
    float sum, avg;
    float numeroaleatorio();

    srand(seed);              /* gera numero aleatorio */
    sum = 0.0;                
    for (i=0; i<1000; i++)
        sum=sum+numeroaleatorio();    
    avg = sum/1000.0;
    if (avg < 0.25 || avg > 0.75) {
        exit(0);
    }

    ntolayer3 = 0;
    nlost = 0;
    ncorrupt = 0;

    time_local=0;               
    generate_next_arrival();
}

/*************************************************************/
int isNumber(char *input)
{
    while (*input){
        if (!isdigit(*input))
            return 0;
        else
            input += 1;
    }

    return 1;
}

int read_arg_int(char c)
{
    if(!isNumber(optarg)) {
        fprintf(stderr, "Valor inválido para -%c\n", c);
        exit(-1);
    }
    return atoi(optarg);
}

float read_arg_float(char c)
{
    float val = atof(optarg);
    if(val < 0.0 || val > 1.0){
        fprintf(stderr, "Valor inválido para -%c\n", c);
        exit(-1);
    }
    return val;
}

void display_usage(char *filename)
{
    printf("Entradas:\n %s -s Seed -w tamanho janela -m numero de mensagens para simular -l Perda -c Conrompidos -t Tempo médio entre mensagens da camada do remetente -v menor que 2 sem info > 2 com info Rastreamento\n", filename);
}

int main(int argc, char **argv)
{
    struct event *eventptr;
    struct msg  msg2give;
    struct pkt  pkt2give;

    int i,j;
    char c;

    int opt;
    int seed;

    //verifica o numero de argumentos
    if(argc != 15){
        fprintf(stderr, "Falta Argumentos!\n");
        display_usage(argv[0]);
        return -1;
    }

    while((opt = getopt(argc, argv,"s:w:m:l:c:t:v:")) != -1){
        switch (opt){
            case 's':   seed = read_arg_int(opt);
                break;
            case 'w':   win_size = read_arg_int(opt);
                break;
            case 'm': 	nsimmax = read_arg_int(opt);
                break;
            case 'l': 	lossprob = read_arg_float(opt);
                break;
            case 'c': 	corruptprob = read_arg_float(opt);
                break;
            case 't': 	if((lambda = atof(optarg)) <= 0.0){
            				fprintf(stderr, "Valor invalido para -%c\n", opt);
                exit(-1);
            }
                break;
            case 'v': 	TRACE = read_arg_int(opt);
                break;
            case '?':
           	default:    fprintf(stderr, "Argumentos invalidos!\n");
                display_usage(argv[0]);
                return -1;
        }
    }

    init(seed);
    A_init();
    B_init();

    while (1) {
        eventptr = evlist;            
        if (eventptr==NULL)
            goto terminate;
        evlist = evlist->next;        
        if (evlist!=NULL)
            evlist->prev=NULL;
        if (TRACE>=2) {
            printf("\nTempo Evento: %f,",eventptr->evtime);
            printf("  tipo: %d",eventptr->evtype);
            if (eventptr->evtype==0)
                printf(", interrupção do tempo  ");
            else if (eventptr->evtype==1)
                printf(", fromlayer5 ");
            else
                printf(", fromlayer3 ");
            printf(" entidade: %d\n",eventptr->eventity);
        }
        time_local = eventptr->evtime;        /* update do tempo para proximo evento */
        if (nsim==nsimmax)
            break;                     
        if (eventptr->evtype == FROM_LAYER5 ) {
            generate_next_arrival();   
            j = nsim % 26;
            for (i=0; i<20; i++)
                msg2give.data[i] = 97 + j;
            if (TRACE>2) {
                printf("          MAIN LOOP: dados para verificar: ");
                for (i=0; i<20; i++)
                    printf("%c", msg2give.data[i]);
                printf("\n");
            }
            nsim++;
            if (eventptr->eventity == A)
            {
                A_application += 1;
                A_output(msg2give);
            }
        }
        else if (eventptr->evtype ==  FROM_LAYER3) {
            pkt2give.seqnum = eventptr->pktptr->seqnum;
            pkt2give.acknum = eventptr->pktptr->acknum;
            pkt2give.checksum = eventptr->pktptr->checksum;
            for (i=0; i<20; i++)
                pkt2give.payload[i] = eventptr->pktptr->payload[i];
            if (eventptr->eventity ==A)      
                A_input(pkt2give);            
            else
            {
                B_transport += 1;
                B_input(pkt2give);
            }
            free(eventptr->pktptr);
        }
        else if (eventptr->evtype ==  TIMER_INTERRUPT) {
            if (eventptr->eventity == A)
                A_timerinterrupt();
        }
        else  {
            printf(" tipo de evento desconhecido \n");
        }
        free(eventptr);
    }

terminate:
    //Do NOT change any of the following printfs
    printf(" Simulador terminou no tempo %f\n depois de enviar %d mensagens \n",time_local,nsim);

    printf("\n");
    printf("[PA2]%d pacotes enviadas da camada de aplicação do remetente A [/PA2]\n", A_application);
    printf("[PA2]%d pacotes enviadas da camada de transporte do remetente A [/PA2]\n", A_transport);
    printf("[PA2]%d pacotes recebidos na camada de transporte do receptor B [/PA2]\n", B_transport);
    printf("[PA2]%d pacotes recebidos na camada de aplicação do receptor B [/PA2]\n", B_application);
    printf("# %d ACKs com NÚMEROS DE SEQUÊNCIA INVÁLIDOS EM A\n",ackWithInvalidSeqNum);      
    printf("# %d Pacotes corrompidos RCVD em B\n", pacoteCorrompido);                                   
    printf("# %d ACKs corrompidos RCVD em A\n",ACKcorrompido);                                         
    printf("[PA2]Tempo total: %f [/PA2]\n", time_local);
    printf("[PA2]Taxa de transferência: %f pacotes[/PA2]\n", B_application/time_local);

    printf("[agautam2]%d acks foram recebidos em A[/agautam2]\n",ackCount);               
    float efficiency=0.0;
    efficiency=(float)ackCount/A_application;
    printf("[agautam2]Eficiencia e: %f [/agautam2]\n",efficiency);               

    return 0;
}

void printevlist()
{
    struct event *q;
    int i;
    printf("--------------\nEvent List Follows:\n");
    for(q = evlist; q!=NULL; q=q->next) {
        printf("Event time: %f, type: %d entity: %d\n",q->evtime,q->evtype,q->eventity);
    }
    printf("--------------\n");
}



void stoptimer(int AorB)
{
    struct event *q,*qold;

    if (TRACE>2)
        printf("          STOP TEMPO EM: %f\n",time_local);
    
    for (q=evlist; q!=NULL ; q = q->next)
        if ( (q->evtype==TIMER_INTERRUPT  && q->eventity==AorB) ) {
            if (q->next==NULL && q->prev==NULL)
                evlist=NULL;         
            else if (q->next==NULL) 
                q->prev->next = NULL;
            else if (q==evlist) { 
                q->next->prev=NULL;
                evlist = q->next;
            }
            else {     
                q->next->prev = q->prev;
                q->prev->next =  q->next;
            }
            free(q);
            return;
        }
    printf("Warning: nao e possivel cancelar tempo.\n");
}


void starttimer(int AorB,float increment)
{

    struct event *q;
    struct event *evptr;
    if (TRACE>2)
        printf("          TEMPO INICIADO EM: %f\n",time_local);
    for (q=evlist; q!=NULL ; q = q->next)
        if ( (q->evtype==TIMER_INTERRUPT  && q->eventity==AorB) ) {
            printf("Warning:  tentativa de iniciar um cronômetro que já foi iniciado\n");
            return;
        }
    evptr = (struct event *)malloc(sizeof(struct event));
    evptr->evtime =  time_local + increment;
    evptr->evtype =  TIMER_INTERRUPT;
    evptr->eventity = AorB;
    insereEvento(evptr);
}


void tolayer3(int AorB,struct pkt pacote)
{
    struct pkt *mypktptr;
    struct event *evptr,*q;
    float lastime, x, numeroaleatorio();
    int i;

    ntolayer3++;

    if(AorB == 0) A_transport += 1;

    if (numeroaleatorio() < lossprob)  {
        nlost++;
        if (TRACE>0)
            printf("          CAMADA 3: pacote sendo perdido\n");        
            return;
    }

    mypktptr = (struct pkt *)malloc(sizeof(struct pkt));
    mypktptr->seqnum = pacote.seqnum;
    mypktptr->acknum = pacote.acknum;
    mypktptr->checksum = pacote.checksum;
    for (i=0; i<20; i++)
        mypktptr->payload[i] = pacote.payload[i];
    if (TRACE>2)  {
        printf("          CAMADA 3: seq: %d, ack %d, check: %d ", mypktptr->seqnum,
               mypktptr->acknum,  mypktptr->checksum);
        for (i=0; i<20; i++)
            printf("%c",mypktptr->payload[i]);
        printf("\n");
    }

    evptr = (struct event *)malloc(sizeof(struct event));
    evptr->evtype =  FROM_LAYER3;   
    evptr->eventity = (AorB+1) % 2; 
    evptr->pktptr = mypktptr;       
    lastime = time_local;
    for (q=evlist; q!=NULL ; q = q->next)
        if ( (q->evtype==FROM_LAYER3  && q->eventity==evptr->eventity) )
            lastime = q->evtime;
    evptr->evtime =  lastime + 1 + 9*numeroaleatorio();

    /* simula corrompido */
    if (numeroaleatorio() < corruptprob)
    {
        ncorrupt++;
        if ( (x = numeroaleatorio()) < .75)
            mypktptr->payload[0]='Z';   
        else if (x < .875)
            mypktptr->seqnum = 999999;
        else
            mypktptr->acknum = 999999;
        if (TRACE>0)
        {}
    }

    if (TRACE>2)
        printf("          CAMADA 3: chegada no outro lado\n");
    insereEvento(evptr);
}

void tolayer5(int AorB,char *datasent)
{

    int i;
    if (TRACE>2) {
        printf("          CAMADA 5: DADOS RECEBIDOS: ");
        for (i=0; i<20; i++)
            printf("%c",datasent[i]);
        printf("\n");
    }
    if(AorB == 1) B_application += 1;
}

int getwinsize()
{
    return win_size;
}

float get_sim_time()
{
    return time_local;
}


