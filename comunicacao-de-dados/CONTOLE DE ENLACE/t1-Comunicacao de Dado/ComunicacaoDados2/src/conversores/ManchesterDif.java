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
public class ManchesterDif {
     // Defines:
    private static final int POSITIVO = 1;
    private static final int NEUTRO = 0;
    private static final int NEGATIVO = -1;
    public ArrayList<Integer> dadoMod;
    
    public ManchesterDif() {
        dadoMod =  new ArrayList<>();
        dadoMod.clear();
    }
    public ArrayList<Integer> ManDifMOD (String str) {
        dadoMod.clear();
        char dado;
        dadoMod.add(POSITIVO);
        dadoMod.add(POSITIVO);
        for (int i = 0; i < str.length(); i++) {
            dado = str.charAt(i);
            if (dado == '0') {
                dadoMod.add(Inverte());
                dadoMod.add(Inverte());
            } else if (dado == '1') {
                dadoMod.add(Ultimo());
                dadoMod.add(Inverte());
            }
        }
        return dadoMod;
    }
    
    private Integer Inverte () {
        return inverte(Ultimo());
    }
    
    private Integer Ultimo() {
        return dadoMod.get(dadoMod.size()-1);
    }
    
    private Integer inverte (Integer temp) {
        if (null == temp) { // Não há o que inverter
            return NEUTRO;
        } else switch (temp) {
            case POSITIVO:
                return NEGATIVO;
            case NEGATIVO:
                return POSITIVO;
            default:
                return NEUTRO;
        }        
    }
    
    
}
