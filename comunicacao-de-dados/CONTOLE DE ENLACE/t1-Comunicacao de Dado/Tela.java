/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package conversores;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Juliano
 */
public class Tela extends Application {
    public XYChart.Series series = new XYChart.Series();
    Button btnNRZL;
    Button btnNRZI; 
    Button btnAMI;
    Button btnPT;
    Button btnMAN;
    Button btnMANDIF;
    Button btnLIMPA;

    public Tela() {
        this.btnNRZL = new Button();
        this.btnNRZI = new Button();
        this.btnAMI = new Button();
        this.btnPT = new Button();
        this.btnMAN = new Button();
        this.btnMANDIF = new Button();
        this.btnLIMPA = new Button();
    }
    
    @Override
    public void start(Stage primaryStage) {
        // Construtores
        AMI ami = new AMI();
        Manchester man = new Manchester();
        NRZ_I nrzi = new NRZ_I();
        NRZ_L nrzl = new NRZ_L();
        Pseudoternario pseud = new Pseudoternario();
        ManchesterDif manD = new ManchesterDif();
        
        // Texto
        TextField dados = new TextField();
        dados.setMaxWidth(1000);
        Botoes(ami, man, nrzi,nrzl,pseud,manD, dados);
        // Grafico
        CategoryAxis eixoX = new CategoryAxis();
        NumberAxis eixoY = new NumberAxis();       
        
        final LineChart<String,Number> lineChart = new LineChart<>(eixoX,eixoY);
        lineChart.setMinSize(800, 400);
        eixoY.setAutoRanging(false);
        eixoY.setLowerBound(-2);
        eixoY.setUpperBound(2);
        eixoY.setTickUnit(1);
        lineChart.getData().add(series);
        
        // VBOX`s e HBOX`s
        VBox vb1 = new VBox();
        vb1.setAlignment(Pos.TOP_LEFT);
        vb1.setSpacing(10);
        vb1.getChildren().addAll(btnNRZL,btnNRZI, btnAMI);
        VBox vb2 = new VBox();
        vb2.setAlignment(Pos.TOP_LEFT);
        vb2.setSpacing(10);
        vb2.getChildren().addAll(btnPT,btnMAN, btnMANDIF);
        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER);
        hb.setSpacing(2);
        hb.getChildren().addAll(vb1,vb2);
        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        vb.setSpacing(10);
        vb.getChildren().addAll(dados, hb,btnLIMPA);
        HBox hb1 = new HBox();
        hb1.setAlignment(Pos.BASELINE_LEFT);
        hb1.setSpacing(2);
        hb1.getChildren().addAll(vb,lineChart);
        Scene scene = new Scene(hb1,1200, 400);
        primaryStage.setTitle("Comunicacao de Dados");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void geraGrafico(ArrayList<Integer> sinal){
        series.getData().clear();
        for (int i = 1; i < sinal.size(); i++) {
            try {
                series.getData().add(new XYChart.Data("" + i, sinal.get(i)));
                series.getData().add(new XYChart.Data("" + i, sinal.get(i+1)));
            } catch (Exception e) {
            }
        }
    }
    
    public void Botoes(AMI ami,Manchester man,NRZ_I nrzi,NRZ_L nrzl,Pseudoternario pseud, ManchesterDif manD, TextField dados){
        //gráfico pra nrzl
        btnNRZL.setText("NRZ-L");
        btnNRZL.setPrefSize(220, 35);
        btnNRZL.setOnAction((ActionEvent event) -> {
            ArrayList<Integer> nrzlMod;
            nrzlMod = nrzl.NrzlMOD(dados.getText());
            geraGrafico(nrzlMod);
        });
        
        // gráfico pra nrzi
        btnNRZI.setText("NRZ-I");
        btnNRZI.setPrefSize(220, 35);
        btnNRZI.setOnAction((ActionEvent event) -> {
            ArrayList<Integer> nrziMod;
            nrziMod = nrzi.NrziMOD(dados.getText());
            geraGrafico(nrziMod);
        });
        
        // gráfico pra AMI
        btnAMI.setText("AMI");
        btnAMI.setPrefSize(220, 35);
        btnAMI.setOnAction((ActionEvent event) -> {
            ArrayList<Integer> amiMod;
            amiMod = ami.AmiMOD(dados.getText());
            geraGrafico(amiMod);
        });
        
        // gráfico pra pseudoternário
        btnPT.setText("PSEUDOTERNÁRIO");
        btnPT.setPrefSize(220, 35);
        btnPT.setOnAction((ActionEvent event) -> {
            ArrayList<Integer> pseudMod;
            pseudMod = pseud.PseudMOD(dados.getText());
            geraGrafico(pseudMod);
        });
        
        // gráfico pra manchester
        btnMAN.setText("MANCHESTER");
        btnMAN.setPrefSize(220, 35);
        btnMAN.setOnAction((ActionEvent event) -> {
            ArrayList<Integer> manMod;
            manMod =man.ManMOD(dados.getText());
            geraGrafico(manMod);
        });

        //gráfico pra manchester diferencial
        btnMANDIF.setText("MANCHESTER DIFERENCIAL");
        btnMANDIF.setPrefSize(220, 35);
        btnMANDIF.setOnAction((ActionEvent event) -> {
            ArrayList<Integer> mandifMod;
            mandifMod = manD.ManDifMOD(dados.getText());
            geraGrafico(mandifMod);
        });

        btnLIMPA.setText("LIMPAR");
        btnLIMPA.setPrefSize(220, 35);
        btnLIMPA.setOnAction((ActionEvent event) -> {
            series.getData().clear();
            series.setName("");
            dados.clear();
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}