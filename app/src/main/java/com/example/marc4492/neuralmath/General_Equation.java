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

    String normaliserFois() {
        if (Pattern.matches("(?i)\\w\\((\\-?\\d+(\\.\\d*)?|\\w)\\)=.*", m_equation)) {
            String equationTemp = m_equation.substring(m_equation.indexOf("=") + 1);
            for (int i = 0; i < equationTemp.length() - 1; i++)    //Début de la normalisation des équations : mets un * entre les lettres et les chiffres ainsi que lettres et parenthèses ou chiffre + parenthèses
            {
                String chaineTemp = String.valueOf(equationTemp.charAt(i)) + String.valueOf(equationTemp.charAt(i + 1));

                if (Pattern.matches("(?i)\\d[a-z]", chaineTemp) || Pattern.matches("//", chaineTemp) || Pattern.matches("(?i)\\d\\(", chaineTemp) || Pattern.matches("(?i)([a-e]|[h-m]|[o-r]|[t-z])\\(", chaineTemp)) {
                    String chaine1 = equationTemp.substring(0, i + 1) + "*"; // On sépare le string en 2 à la position qui respecte les conditions.
                    String chaine2 = equationTemp.substring(i + 1);
                    equationTemp = chaine1 + chaine2;
                    System.out.println(equationTemp);
                }
            }
            m_equation = m_equation.replace(m_equation.substring(m_equation.indexOf("=") + 1), equationTemp);
        } else {
            for (int i = 0; i < m_equation.length() - 1; i++)    //Début de la normalisation des équations : mets un * entre les lettres et les chiffres ainsi que lettres et parenthèses ou chiffre + parenthèses
            {
                String chaineTemp = String.valueOf(m_equation.charAt(i)) + String.valueOf(m_equation.charAt(i + 1));

                if (Pattern.matches("(?i)\\d[a-z]", chaineTemp) || Pattern.matches("//", chaineTemp) || Pattern.matches("(?i)\\d\\(", chaineTemp) || Pattern.matches("(?i)([a-e]|[h-m]|[o-r]|[t-z])\\(", chaineTemp)) {
                    String chaine1 = m_equation.substring(0, i + 1) + "*"; // On sépare le string en 2 à la position qui respecte les conditions.
                    String chaine2 = m_equation.substring(i + 1);
                    m_equation = chaine1 + chaine2;
                    System.out.println(m_equation);
                }
            }
        }
        return m_equation;
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
