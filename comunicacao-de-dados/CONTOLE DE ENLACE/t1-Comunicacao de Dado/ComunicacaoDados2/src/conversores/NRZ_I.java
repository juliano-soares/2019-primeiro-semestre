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
public class NRZ_I {
    private static final int POSITIVO = 1;
    private static final int NEUTRO = 0;
    private static final int NEGATIVO = -1;
    public ArrayList<Integer> dadoMod;
    
    public NRZ_I() {
        dadoMod =  new ArrayList<>();
        dadoMod.clear();
    }
    
    public ArrayList<Integer> NrziMOD (String str) {
        dadoMod.clear();
        char c;
        c = str.charAt(0);
        if(c == 0){
            dadoMod.add(POSITIVO);
            dadoMod.add(POSITIVO);
        }else{
            dadoMod.add(NEGATIVO);
            dadoMod.add(NEGATIVO);
        }
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '1') {
                Integer aux = Inverte();
                dadoMod.add(aux);
                dadoMod.add(aux);
            } else if(c == '0') {
                Integer aux = Ultimo();
                dadoMod.add(aux);
                dadoMod.add(aux);
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
        if (null == temp) {
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
