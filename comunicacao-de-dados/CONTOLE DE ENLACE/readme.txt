FEITO POR JULIANO LEONARDO SOARES


O programa nao foi feito com sockets pois fiquei com muita dificuldade de entender
na minha inplementacao com sockets comecou a causar varios bugs quando tentei simular erros
entao larguei dela e resolvi implementar esta 

OBS: Foi feita em linux entao pode ser que tenha bibliotecas que podem entrar em conflito 
se usar outro sistema

LINGUAGEM C++
 
entao fiz um simulador

passos do programa

*main
1- verifica na main os argumentos mandados
	read_arg_int()
	read_arg_float()
2 - inicializa o programa com a seed dada
	void init(int seed)
	2.1 - gera um numero aleatorio
	seta os dados para zero
	ntolayer3
	nlost
	ncorrupt
	timr_local
	2,2 -  gera a mensagem/pacote 
	void generate_next_arrival()
		2.2.1 insere/gera um Evento neste pacote
		void insereEvento(struct event *p)

3- inicia A
	A_init()
	seta dados em a
	3.1- tamanho janela
	getwinsize()
	3.2- inicia o timer
	starttimer()

4- inicia B
	B_init()
	seta dados em b

5- while ate os eventos terminarem
	e nesse momento que comeca a simulacao
	baseado na lista de eventos 
	- basicamente o meu programa cria eventos para simular
	uma transferencia tcp com select repeat baseada em eventos

	ele verifica o dado 
		se deu erro com checksum
		se deu erro com seqnum 
		se deu erro com ack
		se deu erro com tempo

6- apos simular os erros ele da um print de algumas informacoes


7- resumo do 5

	if evento == NULL
		termina o programa

	if a lista de eventos != NULL
		passa para proximo evento

	if trace >= 2
		print com informacoes
		- tempo de evento
		- tipo
		- se foi interrupcao por tempo ou FROM_LAYER5 ou FROM_LAYER3-entidade

	if nsim == nsimmax
		numero de mensagem == ao maximo de mensagens
		termina o programa

	if tipo de evento == FROM_LAYER5
		generate_next_arrival()
		if trace > 2
			printa informacoes
		if evento-> eventity == A
			A_application ++
			A_output()

	if tipo de evento == FROM_LAYER3
		if evento-> eventity == A
			A_input()
		else
			B_transport ++
			B_input()
		free no evento

	if tipo de evento == TIMER_INTERRUPT
		if eventptr->eventity == A
			A_timeinterrupt()

	free no evento


8- O que faz A_input
	inicia o pacote
	pega o checksum
	
	verifyChecksum=pacote.acknum+pacote.seqnum;
	payloadChecksum=payloadChecksum+pacote.payload[i];
	verifyChecksum=(verifyChecksum+payloadChecksum)*2;

	janela deslizante = paga tamanho janela + baseA

	-verifica se pacote corrompido
	bom_ruim = isCorrupt()
	if bom_ruim == 1
		if(pacote.acknum>=baseA)
			if(pacote.acknum<slidingWindow)
				setToTrue() ack
				while(y<=slidingWindow)

				while(g<slidingWindow)
					TimeoutAdaptativo();
					serve para verificar se buffer esta cheio
					se verificar o numero de ack / pelo numero A_output
					se >= 1					tempo - 12
					se 0,6 > entre < 1		tempo - 11
					se 0,3 > entre < 0,6	tempo - 10
					se < 0,3  				tempo - 9

	if(Bom_Ruim==0)
		ACKcorrompido++;
		add dados para visualizacao
	else
		ackWithInvalidSeqNum++;
		add dados para visualizacao

* chamado da camada 5, passou os dados para serem enviados para o outro lado 
9- O que faz A_output
	-cria dados de saida
	setToZero(pacote.checksum);
    setToZero(pacote.acknum);
    pacote.seqnum=buscaA;
    storeAndpacketize(buscaA, pacote, mensagem);
    slidingWindow=baseA+tamanhoJanela;
	ptrA[y].sendingTime=get_sim_time();
	atualiza o buscador de mensagens
	vai criando as mensagems e enviando para a janela e a cada cinco envios verifica 
	o TimeoutAdaptativo


chamado quando o temporizador de A termina
10- O que faz A_timeinterrupt
	compara o tempo decorrido desde que cada pacote foi enviado com o tempo limite
	y = baseA
	while(y<slidingWindow)
		if(ptrA[y].pushedtToL3==true)
            if(ptrA[y].pushedtToL5==false)
            	if (tempoDecorrido>=incrementoTemporizador)
            		if(at%5==0)
            			TimeoutAdaptativo();

    starttimer(0,1.0);
	-****
	starttimer()
	pega o horario e insere o evento como TIMER_INTERRUPT

	stoptimer()
	para o timer de algum

11- O que faz B_input
	verifica se dado recebido
	e bom_ruim

	se bom_ruim == 0
		pacoteCorrompido++

	verifica se pacote recebido e repetido
		caso sim true

	se bom_ruim == 1
		e pacote repetido == false
		retorna a confirmacao do pacote
		tolayer5()

	se bom_ruim == 1
		e pacote repetido == true
		retorna confirmacao 

		tolayer3()


12- tolayer3()
	simula pacote perdido
	simula corrompido 

13- tolayer5()
	simula dados recebidos com sucesso
