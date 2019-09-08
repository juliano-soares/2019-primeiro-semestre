/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conversores;

import java.util.ArrayList;

/**
 *
 * @author Juliano
 */
public class AMI {
    // Defines:
    private static final int POSITIVO = -1;
    private static final int NEUTRO = 0;
    private static final int NEGATIVO = 1;
    public ArrayList<Integer> dadoMod;
    
    public AMI() {
        //Array para adicional o dado que foi modificado
        dadoMod =  new ArrayList<>();
        dadoMod.clear();
    }
    // Bipolar-AMI
    // 0 = NEUTRO
    // 1 = POSITIVO/NEGATIVO (inverso do último)
    public ArrayList<Integer> AmiMOD (String str) {
        dadoMod.clear();
        char dado;
        boolean positivo = true;
        dadoMod.add(POSITIVO);
        dadoMod.add(POSITIVO);
        for (int i = 0; i < str.length(); i++) {
            dado = str.charAt(i);
            if (dado == '1') {
                if (positivo == true) { 
                    dadoMod.add(NEGATIVO);
                    dadoMod.add(NEGATIVO);
                    positivo = false;
                } else { 
                    dadoMod.add(POSITIVO);
                    dadoMod.add(POSITIVO);  
                    positivo = true;
                }
            }
            else if (dado == '0') {
                dadoMod.add(NEUTRO);
                dadoMod.add(NEUTRO);
            }
        }
        return dadoMod;
    }
}
