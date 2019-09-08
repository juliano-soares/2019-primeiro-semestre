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
public class Manchester {
    // Defines:
    private static final int POSITIVO = 1;
    private static final int NEGATIVO = -1;
    public ArrayList<Integer> dadoMod;
    
    public Manchester() {
        dadoMod =  new ArrayList<>();
        dadoMod.clear();
    }
    // 0 = POSITIVO e NEGATIVO, 1 é sinal NEGATIVO e POSITIVO
    public ArrayList<Integer> ManMOD (String str) {
        dadoMod.clear();
        char c;
        dadoMod.add(POSITIVO);
        dadoMod.add(POSITIVO);
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '0') {
                dadoMod.add(POSITIVO);
                dadoMod.add(NEGATIVO);
            } else if (c == '1') {
                dadoMod.add(NEGATIVO);
                dadoMod.add(POSITIVO);
            }
        }
        return dadoMod;
    }
}
