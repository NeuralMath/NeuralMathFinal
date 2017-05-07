package com.example.marc4492.neuralmath;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe qui résout des trucs si on a un y
 * Created by Alex on 29/03/2017.
 */

class TrouverY extends General_Equation {
    private String valueOfX = "", m_separated_equation = "", m_FirstEquationHalf = "";
    private double x, y, nestedValue;
    private int indexI, indexF;
    private ArrayList<Integer> m_EtapesGrasI;
    private ArrayList<Integer> m_EtapesGrasF;


    TrouverY(String equation) {
        super(equation);
        m_equation = normaliserFois();
        indexI = 0;
        indexF = m_equation.length() - 1;
        m_FirstEquationHalf = m_equation.substring(0, m_equation.indexOf("=") + 1);
        m_equation = m_equation.substring(m_equation.indexOf("=") + 1);
        m_separated_equation = m_equation;
        nestedValue = 0;
        m_EtapesGrasI = new ArrayList<>();
        m_EtapesGrasF = new ArrayList<>();

        FindValueOfX();
        ReplaceValueOfX();
        Simplifier();
        y = nestedValue;

    }

    /**
     * Méthode qui trouve la valeur de x entre les parenthèses d'une fonction
     */
    private void FindValueOfX() {
        for (int i = 0; i < m_FirstEquationHalf.length(); i++) {

            if (Pattern.matches("(\\d)|\\.", String.valueOf(m_FirstEquationHalf.charAt(i))))
                valueOfX += String.valueOf(m_FirstEquationHalf.charAt(i));

            if (m_FirstEquationHalf.charAt(i) == ')') break;

        }
        x = Double.parseDouble(valueOfX);
    }

    /**
     * Méthode qui remplace les 'x' dans les équations par leur valeur numérique donnée par la fonction FindValueOfX
     */
    private void ReplaceValueOfX() {
        if (m_equation.contains("x")) {
            Matcher m = Pattern.compile("x").matcher(m_equation); //http://stackoverflow.com/questions/8938498/get-the-index-of-a-pattern-in-a-string-using-regex
            while (m.find()) {
                mettreEnGras(m.start(), m.end());
            }

            PrintLine();
            m_EtapesText.add("On remplace la valeur de x dans l'équation");
            m_equation = m_equation.replace("x", String.format("%." + String.valueOf(nbDecimales) + "f", x)); // Remplace les "x" dans l'équation par la valeur numérique du x.
        }


    }

    /**
     * Méthode de résolution des équations qui change la valeur de y.
     */
    private void Simplifier() {
        nestedEquation(m_equation);
        while (m_equation.length() != m_separated_equation.length())     //Tant que l'équation séparée n'est pas de la même taille que l'équation complète
        {
            String operator1 = "";
            String operator2 = "";
            boolean operateurTrigo = false;

            nestedEquation(m_equation);
            if (m_separated_equation.contains("E"))
                m_separated_equation = m_separated_equation.replaceAll("E", "*10^");
            nestedValue = evaluateString(m_separated_equation);
            ajouterUneEtape(indexI, indexF, " < Priorité des opérations >  = " + String.valueOf(String.format("%." + String.valueOf(nbDecimales) + "f", nestedValue)));

            if (indexI != 0) {
                int i = indexI;
                char val;
                while (Character.isLetter(m_equation.charAt(i - 1)) && i != 0) {
                    operator1 = m_equation.charAt(i - 1) + operator1;
                    i--;
                    if (i == 0) break;
                }

                String tempStringRemplacerOperateur = m_equation.substring(i, indexI) + m_separated_equation;
                if (m_equation.contains("E")) m_equation = m_equation.replaceAll("E", "*10^");

                if (operator1.contains("arcsin")) {
                    nestedValue = Math.asin(nestedValue);
                    ajouterUneEtape(indexI - 6, indexF, "On évalue la valeur du sinus^-1 de " + m_separated_equation + " = " + String.format("%." + String.valueOf(nbDecimales) + "f", nestedValue));
                    m_equation = m_equation.replace(tempStringRemplacerOperateur, String.valueOf(nestedValue));
                    operateurTrigo = true;
                } else if (operator1.contains("arccos")) {
                    nestedValue = Math.acos(nestedValue);
                    ajouterUneEtape(indexI - 6, indexF, "On évalue la valeur du cosinus^-1 de " + m_separated_equation + " = " + String.format("%." + String.valueOf(nbDecimales) + "f", nestedValue));
                    m_equation = m_equation.replace(tempStringRemplacerOperateur, String.valueOf(nestedValue));
                    operateurTrigo = true;
                } else if (operator1.contains("arctan")) {
                    nestedValue = Math.atan(nestedValue);
                    ajouterUneEtape(indexI - 6, indexF, "On évalue la valeur de tangente^-1 de " + m_separated_equation + " = " + String.format("%." + String.valueOf(nbDecimales) + "f", nestedValue));
                    m_equation = m_equation.replace(tempStringRemplacerOperateur, String.valueOf(nestedValue));
                    operateurTrigo = true;
                } else if (operator1.contains("sin")) {
                    nestedValue = Math.sin(nestedValue);
                    ajouterUneEtape(indexI - 3, indexF, "On évalue la valeur du sinus de " + m_separated_equation + " = " + String.format("%." + String.valueOf(nbDecimales) + "f", nestedValue));
                    m_equation = m_equation.replace(tempStringRemplacerOperateur, String.valueOf(nestedValue));
                    operateurTrigo = true;
                } else if (operator1.contains("cos")) {
                    nestedValue = Math.cos(nestedValue);
                    ajouterUneEtape(indexI - 3, indexF, "On évalue la valeur du cosinus de " + m_separated_equation + " = " + String.format("%." + String.valueOf(nbDecimales) + "f", nestedValue));
                    m_equation = m_equation.replace(tempStringRemplacerOperateur, String.valueOf(nestedValue));
                    operateurTrigo = true;
                } else if (operator1.contains("tan")) {
                    nestedValue = Math.tan(nestedValue);
                    ajouterUneEtape(indexI - 3, indexF, "On évalue la valeur de la tangente de " + m_separated_equation + " = " + String.format("%." + String.valueOf(nbDecimales) + "f", nestedValue));
                    m_equation = m_equation.replace(tempStringRemplacerOperateur, String.valueOf(nestedValue));
                    operateurTrigo = true;
                } else if (operator1.contains("ln")) {
                    nestedValue = Math.log(nestedValue);
                    ajouterUneEtape(indexI - 2, indexF, "On évalue la valeur logarithme en base e de " + m_separated_equation + " = " + String.format("%." + String.valueOf(nbDecimales) + "f", nestedValue));
                    m_equation = m_equation.replace(tempStringRemplacerOperateur, String.valueOf(nestedValue));
                    operateurTrigo = true;
                } else if (operator1.contains("log")) {
                    nestedValue = Math.log10(nestedValue);
                    ajouterUneEtape(indexI - 3, indexF, "On évalue la valeur logarithme en base 10 de " + m_separated_equation + " = " + String.format("%." + String.valueOf(nbDecimales) + "f", nestedValue));
                    m_equation = m_equation.replace(tempStringRemplacerOperateur, String.valueOf(nestedValue));
                    operateurTrigo = true;

                } else if (m_equation.charAt(indexI - 1) == '_') {
                    String s_Base = "";
                    do {
                        i--;
                        val = m_equation.charAt(i);
                        s_Base = val + s_Base;
                        operator1 = val + operator1;

                    }
                    while ((i != 0) && val != '_');    //sort de la boucle si le caractere correspond à un _.

                    do {
                        i--;
                        val = m_equation.charAt(i);
                        operator1 = val + operator1;
                    } while (i != 0 && Character.isLetter(val));


                    Double base = Double.parseDouble(s_Base.substring(1));
                    nestedValue = (Math.log(nestedValue)) / Math.log(base);
                    m_equation = m_equation.replace(operator1.substring(1) + m_separated_equation, String.valueOf(nestedValue));
                    m_EtapesText.add("On évalue la valeur du logarithme de " + nestedEquation(m_equation) + "\n Identité: Log en base a de b = (Log en base x de (b)) / Log en base x de (a) \n (ln" + m_separated_equation + ") / ln(" + s_Base + ")");
                    operateurTrigo = true;
                    break;
                }
                if (m_equation.contains("NaN")) {
                    break;
                }
                if (m_equation.contains("I")) {
                    break;
                }


                nestedEquation(m_equation);
            }

            if (indexF != m_equation.length() - 1) {
                int i = indexF;
                char val;
                while (Character.isLetter(m_equation.charAt(i + 1)) && i != m_equation.length() - 1) {
                    operator2 += m_equation.charAt(i + 1);
                    i++;
                    if (i == m_equation.length() - 1) break;
                }

                if (m_equation.charAt(indexF + 1) == '^') {
                    do {
                        i++;
                        val = m_equation.charAt(i);
                        operator2 += val;

                    }
                    while ((i != m_equation.length() - 1) && (Character.isDigit(m_equation.charAt(i + 1)) || m_equation.charAt(i + 1) == '.' || m_equation.charAt(i + 1) == '-'));


                    Double exposant = Double.parseDouble(operator2.substring(1));
                    ajouterUneEtape(indexI, i, "On évalue  " + m_separated_equation + " à l'exposant " + exposant);
                    nestedValue = Math.pow(nestedValue, exposant);
                    m_equation = m_equation.replace(m_separated_equation + operator2, String.valueOf(nestedValue));
                    nestedEquation(m_equation);

                }
            }

            if (!operateurTrigo) {
                m_separated_equation = m_separated_equation.replaceAll("E", '*' + "10^");
                ajouterUneEtape(indexI, indexF, "Remplacer la valeur de l'équation entre les parenthèses " + m_separated_equation);
                m_equation = m_equation.replace(m_separated_equation, String.valueOf(nestedValue));
            }
            nestedEquation(m_equation);
        }

        if (!m_equation.contains("NaN") && !m_equation.contains("I")) {
            ajouterUneEtape(0, m_equation.length() - 1, "< Priorité des opérations >");
            m_equation = m_equation.replaceAll("[()]", "");
            if (m_equation.contains("E")) m_equation = m_equation.replaceAll("E", "*10^");
            nestedValue = evaluateString(m_equation);
        } else {
            ajouterUneEtape(0, m_equation.length() - 1, "< Impossible de résoudre >");
        }


    }


    /**
     * Méthode qui cherche la partie de l'équation qui est la plus entourée par des parenthèses.
     *
     * @param equation Une string prise en argument afin de rechercher la String la plus creuse dans les parenthèses.
     * @return Un string correspondant à la partie la plus creuse de l'équation
     */
    private String nestedEquation(String equation) {
        int parentheses = 0, parenthesesMax = 0;
        indexI = 0;
        indexF = equation.length() - 1;

        for (int i = 0; i < equation.length(); i++) {
            if ("(".equals(m_equation.charAt(i) + "")) {
                parentheses++;
                if (parenthesesMax <= parentheses) {
                    parenthesesMax = parentheses;
                    indexI = i;
                }
            }
            if (")".equals(m_equation.charAt(i) + "")) {
                parentheses--;
                if (parentheses == parenthesesMax - 1) {
                    indexF = i;
                }
            }

        }

        if (indexF == equation.length() - 1 && indexI == 0) m_separated_equation = m_equation;
        else m_separated_equation = equation.substring(indexI, indexF + 1);
        return m_separated_equation;
    }

    /**
     * Méthode qui ajoute les index de début et de fin à leurs liste pour mettre en gras.
     *
     * @param m_Debut Index de début de la mise en gras
     * @param m_Fin   Index de fin de la mise en gras
     */
    private void mettreEnGras(int m_Debut, int m_Fin) {
        m_EtapesGrasI.add(m_Debut);
        m_EtapesGrasF.add(m_Fin);
    }

    /**
     * Fonction qui sert à évaluer une string en tant qu'expression mathématique .
     * Le code provient de Boann à http://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form
     *
     * @param str Une string qui peut être convertie en valeur mathématique.
     * @return La valeur en double de la String
     */
    private double evaluateString(String str) {
        final String str2 = str.replaceAll(",", "."); //Remplace la décimale , par une décimale .
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str2.length()) ? str2.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str2.length())
                    return Double.NaN;//throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str2.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str2.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    return Double.NaN;
                    //throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) {
                    double exposant = parseFactor();
                    x = Math.pow(x, exposant); // exponentiation
                }
                if (eat('!'))
                    try{
                        if(x % 1 != 0) throw new NumberFormatException();
                        x = factoriel(x);
                        return x;
                    } catch(NumberFormatException e) {
                        System.out.print(x + "is not an integer");
                        x = Double.NaN;
                    }
                return x;
            }
        }.parse();
    }

    private double factoriel(double nombre) {
        if (nombre - 1 > 0) return nombre * factoriel(nombre - 1);
        else return 1;
    }

    private void ajouterUneEtape(int m_debut, int m_fin, String m_explication) {
        int nbDecimales = PrintLine() + 1;
        m_EtapesText.add(m_explication);
        mettreEnGras(m_debut, m_fin + nbDecimales);
    }

    //Getters, Setters
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    String getM_FirstEquationHalf() {
        return m_FirstEquationHalf;
    }

    ArrayList<Integer> getM_EtapesGrasI() {
        return m_EtapesGrasI;
    }

    ArrayList<Integer> getM_EtapesGrasF() {
        return m_EtapesGrasF;
    }
}
