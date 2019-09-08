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
public class Pseudoternario {
    private static final int POSITIVO = 1;
    private static final int NEUTRO = 0;
    private static final int NEGATIVO = -1;
    public ArrayList<Integer> dadoMod;
    
    public Pseudoternario() {
        dadoMod =  new ArrayList<>();
        dadoMod.clear();
    }
    
    public ArrayList<Integer> PseudMOD (String str) {
        dadoMod.clear();
        char dado;
        boolean positivo = false;
        dadoMod.add(POSITIVO);
        dadoMod.add(POSITIVO);
        for (int i = 0; i < str.length(); i++) {
            dado = str.charAt(i);
            if (dado == '0') {
                if (positivo == true) { 
                    dadoMod.add(NEGATIVO);
                    dadoMod.add(NEGATIVO);
                    positivo = false;
                } else {
                    dadoMod.add(POSITIVO);
                    dadoMod.add(POSITIVO);  
                    positivo = true;
                }
            }else if (dado == '1') {
                dadoMod.add(NEUTRO);
                dadoMod.add(NEUTRO);
            }
        }
        return dadoMod;
    }
}
