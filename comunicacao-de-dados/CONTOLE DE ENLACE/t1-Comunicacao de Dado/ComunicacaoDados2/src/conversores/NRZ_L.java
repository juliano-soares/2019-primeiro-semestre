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
public class NRZ_L {
    private static final int POSITIBO = 1;
    private static final int NEGATIVO = -1;
    public ArrayList<Integer> dadoMod;
    
    public NRZ_L() {
        dadoMod =  new ArrayList<>();
        dadoMod.clear();
    }
    public ArrayList<Integer> NrzlMOD (String str) {
        dadoMod.clear();
        char c;
        dadoMod.add(POSITIBO);
        dadoMod.add(POSITIBO);
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '1') {
                dadoMod.add(NEGATIVO);
                dadoMod.add(NEGATIVO);
            }else if (c == '0') {
                dadoMod.add(POSITIBO);
                dadoMod.add(POSITIBO);
            }
        }
        return dadoMod;
    }
    
}
