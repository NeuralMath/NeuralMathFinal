package com.example.marc4492.neuralmath;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe mère des équations générales.
 * Une équation générale contient une démarche, des étapes, et une équation de base à résoudre.
 * On mets aussi un nombre de décimales maximales à afficher pour un nombre
 * Created by Alex on 02/05/2017.
 */

class General_Equation {

    protected final int nbDecimales;
    protected ArrayList<String> m_DemarcheText;
    protected ArrayList<String> m_EtapesText;
    protected String m_equation;


    General_Equation(String equationARemplir)
    {
        nbDecimales = 3 ;
        m_DemarcheText = new ArrayList<>() ;
        m_EtapesText = new ArrayList<>() ;
        m_equation = equationARemplir ;

    }
    /**
     * Méthode qui ajoute la ligne de résolution et les étapes dans des Array tout en changeant le nombre de décimales maximales à 3
     */
    int PrintLine()
    {
        String tempString = m_equation;
        String tempString2 = m_equation;
        List<String> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile("(\\d+\\.\\d+)")
                .matcher(tempString);
        while (m.find()) allMatches.add(m.group());
        if (! allMatches.isEmpty()) {
            for(int i = 0; i-1 != allMatches.size()-1;i++)
            {
                String remplacer = allMatches.get(i);
                tempString = tempString.replace(remplacer,String.format( "%."+String.valueOf(nbDecimales)+"f", Double.parseDouble(remplacer) ));

            }
        }
        m_DemarcheText.add(tempString);
        return tempString.length() - tempString2.length();
    }
    ArrayList<String> getM_DemarcheText() {
        return m_DemarcheText;
    }
    ArrayList<String> getM_EtapesText() {
        return m_EtapesText;
    }

    String getM_equation() {
        return m_equation;
    }

}
