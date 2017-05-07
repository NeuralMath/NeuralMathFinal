package com.example.marc4492.neuralmath;

import java.text.DecimalFormat;
import java.util.ArrayList;

class Resolution extends General_Equation
{
    private ArrayList<String> _variables;               //Les variables dans l'equation.
    private ArrayList<Term> _leftTerms;                 //Les termes de l'equation a gauche du egal.
    private ArrayList<Term> _rightTerms;                //Les termes de l'equation a droite du egal.
    private int _equationLegthDisplay;                  //La grandeur de l'equation dans l'affichage. Sert uniquement a l'affichage.

    private DecimalFormat _df = new DecimalFormat("0.####");    //Format de l'affichage.

    private ArrayList<Character> _letters = new ArrayList<>();    //Lettres pouvant etre une variable.

    Resolution(String equation)
    {
        super(equation);
        //Liste des lettres pouvant etre une variable.
        _letters.add('a');
        _letters.add('b');
        _letters.add('c');
        _letters.add('d');
        _letters.add('f');
        _letters.add('g');
        _letters.add('h');
        _letters.add('i');
        _letters.add('j');
        _letters.add('l');
        _letters.add('n');
        _letters.add('o');
        _letters.add('p');
        _letters.add('r');
        _letters.add('s');
        _letters.add('t');
        _letters.add('w');
        _letters.add('x');
        _letters.add('y');
        _letters.add('z');
        _letters.add('α');
        _letters.add('β');
        _letters.add('θ');

        _variables = new ArrayList<>();

        _leftTerms = new ArrayList<>();
        _rightTerms = new ArrayList<>();
        _equationLegthDisplay = 0;

        fillVariables();
    }

    private void fillVariables()
    {
        for (int j = 0; j < _letters.size(); j++)
        {
            for (int i = 0; i < m_equation.length(); i++)
            {
                if (m_equation.charAt(i) == _letters.get(j))
                {
                    _variables.add(m_equation.charAt(i) + "");
                    break;
                }
            }
        }
    }

    String[] getListVar()
    {
        String[] values = new String[_variables.size()];
        return _variables.toArray(values);
    }

    void setVariable(String var)
    {
        int index = _variables.indexOf(var);

        _variables.remove(index);
        _variables.add(0, var);

        normalize(m_equation);
        fillAllTerms();
        manageParenthesis();
        solve();
    }

    void normalize(String e)
    {
        int nbrLength = 0;

        for (int i = 0; i < e.length(); i++)
        {
            for (int j = 0; j < _variables.size(); j++)
            {
                //Si on a une variable et qu'il n'y a pas de coefficient devant.
                if (e.charAt(i) == _variables.get(j).charAt(0) && (i - 1 < 0 || !Character.isDigit(e.charAt(i - 1))))
                {
                    e = e.substring(0, i) + "1" + e.substring(i, e.length());
                    i++;
                    break;
                }
            }
        }

        for (int i = 0; i < e.length(); i++)
        {
            if (Character.isDigit(e.charAt(i)))
            {
                while (i + nbrLength < e.length() && Character.isDigit(e.charAt(i + nbrLength)))
                {
                    nbrLength++;
                }

                //S'il n'y a pas d'operateur devant le nombre.
                if (i < 1 || (e.charAt(i - 1) != '+' && e.charAt(i - 1) != '-' && e.charAt(i - 1) != '*' && e.charAt(i - 1) != '/' && e.charAt(i - 1) != '^'))
                {
                    e = e.substring(0, i) + "+" + e.substring(i, e.length());
                    i++;
                }

                //Si le nombre est suivi d'une variable.
                if (i + nbrLength + 1 > e.length() || (e.charAt(i + nbrLength) != '+' && e.charAt(i + nbrLength) != '-' && e.charAt(i + nbrLength) != '*' && e.charAt(i + nbrLength) != '/' && e.charAt(i + nbrLength) != '^'))
                {
                    for (int j = 0; j < _variables.size(); j++)
                    {
                        //Si le nombre n'est pas un exposant.
                        if (i - nbrLength - 1 < 0 || (e.charAt(i - nbrLength) != '^') && e.charAt(i - nbrLength - 1) != _variables.get(j).charAt(0))
                        {
                            e = e.substring(0, i + nbrLength) + "*" + e.substring(i + nbrLength, e.length());
                            i++;
                            break;
                        }
                    }
                }
                else if (i + nbrLength + 1 > e.length() || (e.charAt(i + nbrLength) == '+' || e.charAt(i + nbrLength) == '-' || e.charAt(i + nbrLength) == '*' || e.charAt(i + nbrLength) == '/' || e.charAt(i + nbrLength) == '^'))
                {
                    for (int j = 0; j < _variables.size(); j++)
                    {
                        //Si le nombre n'est pas un exposant.
                        if (i - nbrLength - 1 < 0 || (e.charAt(i - nbrLength) != '^' && e.charAt(i - nbrLength - 1) != _variables.get(j).charAt(0)))
                        {
                            e = e.substring(0, i + nbrLength) + "*" + _variables.get(1).charAt(0) + "^0" + e.substring(i + nbrLength, e.length());
                            i += 2;
                            break;
                        }
                    }
                }
            }

            nbrLength = 0;
        }

        for (int i = 0; i < e.length(); i++)
        {
            for (int j = 0; j < _variables.size(); j++)
            {
                //S'il n'y a pas d'exposant apres la variable.
                if (e.charAt(i) == _variables.get(j).charAt(0) && (i + 2 > e.length() || (e.charAt(i + 1) != '^' || !Character.isDigit(e.charAt(i + 2)))))
                {
                    e = e.substring(0, i + 1) + "^1" + e.substring(i + 1, e.length());
                    i += 2;
                    break;
                }
            }
        }

        m_equation = e;
    }

    private int findCharPositionInString(String c, String s, int begin)
    {
        for (int i = begin; i < s.length(); i++)
        {
            if (s.charAt(i) == c.charAt(0))
            {
                return i + 1;	//La position juste apres le caractere
            }
        }

        return 0;
    }

    private void fillAllTerms()
    {
        Term temp = new Term();

        for (int i = 0; i < _variables.size(); i++)
        {
            int posNextVar = findCharPositionInString(_variables.get(i), m_equation, 0), posEqual = findCharPositionInString("=", m_equation, 0);

            while (posNextVar != 0)
            {
                for (int j = 0; j < _variables.size(); j++)
                {
                    if (m_equation.charAt(posNextVar - 1) == _variables.get(i).charAt(0))
                    {
                        temp = fillTerm(posNextVar, m_equation);
                    }
                }

                if (posNextVar < posEqual)	//La variable est a gauche du egal.
                {
                    _leftTerms.add(new Term(temp));
                }
                else                            //La variable est a droite du egal
                {
                    _rightTerms.add(new Term(temp));
                }

                temp.erase();

                posNextVar = findCharPositionInString(_variables.get(i), m_equation, posNextVar);
            }
        }

        //Gauche du egal.
        for (int i = 0; i < _leftTerms.size(); i++)
        {
            for (int j = 0; j < (_leftTerms.size() - i) - 1; j++)
            {
                if (_leftTerms.get(j).getPosition() > _leftTerms.get(j + 1).getPosition())
                {
                    temp = new Term(_leftTerms.get(j));
                    _leftTerms.remove(j);
                    _leftTerms.add(j + 1, new Term(temp));
                }
            }
        }

        //Droite du egal.
        for (int i = 0; i < _rightTerms.size(); i++)
        {
            for (int j = 0; j < (_rightTerms.size() - i) - 1; j++)
            {
                if (_rightTerms.get(j).getPosition() > _rightTerms.get(j + 1).getPosition())
                {
                    temp = new Term(_rightTerms.get(j));
                    _rightTerms.remove(j);
                    _rightTerms.add(j + 1, new Term(temp));
                }
            }
        }

        fixSignOperators();

        updateEquation("Equation recue");
    }

    private  Term fillTerm(int pos, String s)//Permettre les parentheses multiples.
    {
        double coeff, exp;
        String temp = "", op;
        ArrayList<String> open = new ArrayList<>(), close = new ArrayList<>();

        //Trouve le coefficient du terme.
        for (int i = pos - 3; i >= 0; i--)  //Recule de 2 positions avant la variable puis ramasse tous les chiffres.
        {
            if (Character.isDigit(s.charAt(i)))
            {
                temp = s.charAt(i) + temp;
            }
            else
            {
                break;
            }
        }
        coeff = Double.parseDouble(temp);

        //Trouve l'exposant du terme.
        temp = "";
        int index = pos + 1;
        //Si l'exposant est negatif.
        if (s.charAt(pos + 1) == '-')
        {
            temp += "-";
            index++;        //Commence au prochain caractere.
        }
        for (; index < s.length(); index++)  //Avance de 1 position apres la variable puis ramasse tous les chiffres.
        {
            if (Character.isDigit(s.charAt(index)))
            {
                temp = temp + s.charAt(index);
            }
            else
            {
                break;
            }
        }
        exp = Double.parseDouble(temp);

        //Trouve l'operateur du terme.
        op = "" + s.charAt(pos - 3 - _df.format(coeff).length());   //Recule de 2 positions avant la variable et du nombre de chiffres du coefficient.

        //Trouve l'operateur devant la parenthese ouvrante s'il y en a une devant le terme. Recommence tant qu'il y a des parentheses devant.
        for (int i = pos - 3 - _df.format(coeff).length() - 1; i > 0; i--)   //Recule de 2 positions avant la variable et du nombre de chiffres du coefficient.
        {
            if(s.charAt(i) == '(')
            {
                open.add("" + s.charAt(i - 1));
            }
            else
            {
                break;
            }
        }

        //Determine s'il y a une parenthese fermante apres le terme. Recommence tant qu'il y a des parentheses apres.
        for (int i = pos + 1 + _df.format(exp).length(); i < s.length(); i++)   //Avance de 1 position apres la variable et du nombre de chiffres d'exposant.
        {
            if(s.charAt(i) == ')')
            {
                close.add("0");
            }
            else
            {
                break;
            }
        }

        return new Term(pos, "" + s.charAt(pos - 1), coeff, exp, op, open, close);
    }

    private void fixSignOperators()
    {
        //Gauche du egal.
        for (int i = 0; i < _leftTerms.size(); i++)
        {
            if (_leftTerms.get(i).getOperator().equals("-"))
            {
                _leftTerms.get(i).setOperator("+");                                           //Affecte un operateur avec un "+" au terme.
                _leftTerms.get(i).setCoefficient(-1 * _leftTerms.get(i).getCoefficient());    //Inverse le signe du coefficient du terme correspondant.
            }
        }

        //Droite du egal.
        for (int i = 0; i < _rightTerms.size(); i++)
        {
            if (_rightTerms.get(i).getOperator().equals("-"))
            {
                _rightTerms.get(i).setOperator("+");                                            //Affecte un operateur avec un "+" au terme.
                _rightTerms.get(i).setCoefficient(-1 * _rightTerms.get(i).getCoefficient());    //Inverse le signe du coefficient du terme correspondant.
            }
        }
    }

    private String simplify(ArrayList<Term> terms)
    {
        String  etape;

        etape = exponent(terms);

        if (etape.equals(""))
        {
            etape = multiplicationDivision(terms);
        }

        if (etape.equals(""))
        {
            etape = additionSubstraction(terms);
        }

        return etape;
    }

    private ArrayList<Term> sortTermsViaGroups(ArrayList<Term> terms)
    {
        ArrayList<Term> termsTemp = new ArrayList<>();
        ArrayList<ArrayList<Term>> groupsTerms = new ArrayList<>();

        for (int i = 0; i < terms.size(); i++)
        {
            //Si le groupe est vide ou que l'operateur du terme n'est pas un "*" ou un "/".
            if (termsTemp.isEmpty() || !terms.get(i).getOperator().equals("+"))
            {
                termsTemp.add(new Term(terms.get(i)));              //Copie le dans le groupe.
            }
            else
            {
                groupsTerms.add(new ArrayList<>(termsTemp));          //Le groupe est complet. Met le dans le ArrayList de groupes.

                termsTemp.clear();                                  //Puis effaces-le.
                i--;                                                //Recommence pour le meme terme, donc decremente i.
            }
        }
        groupsTerms.add(new ArrayList<>(termsTemp));                  //Le dernier groupe est complet. Met le dans le ArrayList de groupes.

        groupsTerms = sortInGroups(groupsTerms);

        //Copie groupsTerms dans terms.
        terms.clear();
        for (int i = 0; i < groupsTerms.size(); i++)
        {
            for (int j = 0; j < groupsTerms.get(i).size(); j++)
            {
                terms.add(new Term(groupsTerms.get(i).get(j)));
            }
        }

        return terms;
    }

    private ArrayList<ArrayList<Term>> sortInGroups(ArrayList<ArrayList<Term>> groups)
    {
        Term temp;
        ArrayList<Term> groupTemp;

        //Tri Bubblesort dans le groupe.
        for (ArrayList<Term> group : groups)
        {
            for (int j = 0; j < group.size(); j++)
            {
                for (int k = 0; k < (group.size() - j) - 1; k++)
                {

                    //Si l'operateur n'est pas "/" et si l'exposant du terme est plus petit que l'exposant du terme suivant, sinon si la valeur absolue du coefficient du terme est plus petit que la valeur absolue du coefficient du terme suivant.
                    if ((!group.get(k).getOperator().equals("/") || !group.get(k + 1).getOperator().equals("/")) && group.get(k).getExponent() < group.get(k + 1).getExponent() || (group.get(k).getExponent() == group.get(k + 1).getExponent() && Math.abs(group.get(k).getCoefficient()) < Math.abs(group.get(k + 1).getCoefficient())))
                    {
                        //Il ne faut pas que les operateurs bougent, c'est pourquoi on ne les echange pas.
                        temp = new Term(group.get(k));

                        group.remove(k);
                        group.add(k, new Term(group.get(k)));

                        group.remove(k + 1);
                        group.add(k, new Term(temp));
                    }

                }
            }
        }

        //Tri Bubblesort entre les groupes.
        for (int i = 0; i < groups.size(); i++)
        {
            for (int j = 0; j < (groups.size() - i) - 1; j++)
            {
                //Si l'operateur n'est pas "/" et si l'exposant du terme est plus petit que l'exposant du terme suivant, sinon si la valeur absolue du coefficient du terme est plus petit que la valeur absolue du coefficient du terme suivant.
                if ((!groups.get(j + 1).get(0).getOperator().equals("/")) && groups.get(j).get(0).getExponent() < groups.get(j + 1).get(0).getExponent() || (groups.get(j).get(0).getExponent() == groups.get(j + 1).get(0).getExponent() && Math.abs(groups.get(j).get(0).getCoefficient()) < Math.abs(groups.get(j + 1).get(0).getCoefficient())))
                {
                    groupTemp = new ArrayList<>(groups.get(j));

                    groups.remove(j);
                    groups.add(j, new ArrayList<>(groups.get(j)));

                    groups.remove(j + 1);
                    groups.add(j + 1, new ArrayList<>(groupTemp));
                }
            }
        }

        return groups;
    }

    private String exponent(ArrayList<Term> terms)
    {
        for (int i = 0; i < terms.size(); i++)
        {
            //Si l'operateur du prochain terme est '^'.
            if (i + 1 < terms.size() && terms.get(i + 1).getOperator().equals("^"))
            {
                terms.get(i).setCoefficient(Math.pow(terms.get(i).getCoefficient(), terms.get(i + 1).getCoefficient()));    //Exponentie le coefficients.
                terms.get(i).setExponent(terms.get(i).getExponent() * terms.get(i + 1).getCoefficient());                   //Multiplie l'exposant.

                for (String close : terms.get(i + 1).getCloseParenthesis())
                {
                    terms.get(i).getCloseParenthesis().add(close);                                                  //Ajoute les parentheses fermantes.
                }

                terms.get(i + 1).setCoefficient(0);
                terms.get(i + 1).setOperator("+");

                terms = removeRedundantTerms(terms);

                return "Exposant";
            }
        }

        return "";
    }

    private String multiplicationDivision(ArrayList<Term> terms)
    {
        String etape = "";

        for (int i = 0; i < terms.size() - 1; i++)
        {
            //Si les variables des termes sont les memes (ou un des deux termes a un exposant de 0) et l'operateur est "*".
            if ((terms.get(i).getCharacter().equals(terms.get(i + 1).getCharacter()) || terms.get(i).getExponent() == 0 || terms.get(i + 1).getExponent() == 0) && terms.get(i + 1).getOperator().equals("*"))
            {
                if (terms.get(i).getExponent() == 0 && terms.get(i + 1).getExponent() != 0)
                {
                    terms.get(i).setCharacter(terms.get(i + 1).getCharacter());
                }

                terms.get(i).setCoefficient(terms.get(i).getCoefficient() * terms.get(i + 1).getCoefficient());                         //Multiplie les coefficients.
                terms.get(i).setExponent(terms.get(i).getExponent() + terms.get(i + 1).getExponent());                                  //Aditionne les exposants.

                for (String close : terms.get(i + 1).getCloseParenthesis())
                {
                    terms.get(i).getCloseParenthesis().add(close);                                                                      //Ajoute les parentheses fermantes.
                }

                terms.get(i + 1).setCoefficient(0);                                                                                     //Met le coefficient du deuxieme terme a 0.
                terms.get(i + 1).setOperator("+");                                                                                      //Met l'operateur du terme a "+" pour qu'il ne le multiplie pas encore.

                terms = removeRedundantTerms(terms);

                etape = "Multiplication/Division";
            }
            //Si les variables des termes sont les memes et l'operateur est "/".
            else if ((terms.get(i).getCharacter().equals(terms.get(i + 1).getCharacter()) || terms.get(i).getExponent() == 0 || terms.get(i + 1).getExponent() == 0) && terms.get(i + 1).getOperator().equals("/"))
            {
                if (terms.get(i).getExponent() == 0 && terms.get(i + 1).getExponent() != 0)
                {
                    terms.get(i).setCharacter(terms.get(i + 1).getCharacter());
                }

                terms.get(i).setCoefficient(terms.get(i).getCoefficient() / terms.get(i + 1).getCoefficient());                         //Divise les coefficients.
                terms.get(i).setExponent(terms.get(i).getExponent() - terms.get(i + 1).getExponent());                                  //Soustrait les exposants.

                for (String close : terms.get(i + 1).getCloseParenthesis())
                {
                    terms.get(i).getCloseParenthesis().add(close);                                                                      //Ajoute les parentheses fermantes.
                }

                terms.get(i + 1).setCoefficient(0);                                                                                     //Met le coefficient du deuxieme terme a 0.
                terms.get(i + 1).setOperator("+");                                                                                      //Met l'operateur du terme a "+" pour qu'il ne le multiplie pas encore.

                terms = removeRedundantTerms(terms);

                etape = "Multiplication/Division";
            }
        }
        return etape;
    }

    private String additionSubstraction(ArrayList<Term> terms)
    {
        String etape = "";

        for (int i = 0; i < terms.size() - 1; i++)
        {
            //Si les variables des termes sont les memes et operateurs sont '+' et les exposants sont les memes.
            if (terms.get(i).getCharacter().equals(terms.get(i + 1).getCharacter()) && terms.get(i).getOperator().equals("+") && terms.get(i + 1).getOperator().equals("+") && terms.get(i).getExponent() == terms.get(i + 1).getExponent())
            {
                terms.get(i).setCoefficient(terms.get(i).getCoefficient() + terms.get(i + 1).getCoefficient());     //Aditionne les coefficients.

                for (String close : terms.get(i + 1).getCloseParenthesis())
                {
                    terms.get(i).getCloseParenthesis().add(close);                                                  //Ajoute les parentheses fermantes.
                }

                terms.get(i + 1).setCoefficient(0);
                terms.get(i + 1).setOperator("+");

                terms = removeRedundantTerms(terms);

                etape = "Adition/Soustraction";
            }
        }

        return etape;
    }

    private ArrayList<Term> removeRedundantTerms(ArrayList<Term> terms)
    {
        for (int i = 0; i < terms.size(); i++)
        {
            //S'il y a plus d'un terme et si le coefficient du terme est 0.
            if (terms.size() > 1 && terms.get(i).getCoefficient() == 0)
            {
                terms.remove(i);
                i--;
            }
        }

        return terms;
    }

    private void solve()
    {
        String etape;

        do
        {
            etape = simplify(_leftTerms);
            updateEquation(etape);
        }
        while(!etape.equals(""));

        _leftTerms = sortTermsViaGroups(_leftTerms);
        updateEquation("Ordonner");

        do
        {
            etape = simplify(_rightTerms);
            updateEquation(etape);
        }
        while(!etape.equals(""));

        _rightTerms = sortTermsViaGroups(_rightTerms);
        updateEquation("Ordonner");

        //Si les coefficients des premiers termes de chaque cote du egal sont differents de 1.
        if (_leftTerms.get(0).getExponent() != 0 || _rightTerms.get(0).getExponent() != 0)
        {
            mainVariableToLeft();

            //Si l'equation est de degre 1.
            if (_leftTerms.get(0).getExponent() <= 1 && _rightTerms.get(0).getExponent() <= 1)
            {
                solveDegreeOne();
            }
            //Si l'equation est de degre 2.
            else if (_leftTerms.get(0).getExponent() <= 2 && _rightTerms.get(0).getExponent() <= 2)
            {
                solveDegreeTwo();
            }
        }
        else if (_leftTerms.get(0).getCoefficient() != _rightTerms.get(0).getCoefficient())
        {
            System.out.println("Solution impossible\n");
        }
    }

    private void mainVariableToLeft()
    {

        //Gauche du egal.
        for (int i = 0; i < _leftTerms.size(); i++)
        {
            if (!_leftTerms.get(i).getCharacter().equals(_variables.get(0)) && (_leftTerms.size() != 1 || _leftTerms.get(0).getCoefficient() != 0))
            {
                transferTerm(i);
                i--;
            }
        }

        //Droite du egal.
        for (int i = 0; i < _rightTerms.size(); i++)
        {
            if (_rightTerms.get(i).getCharacter().equals(_variables.get(0)) && (_rightTerms.size() != 1 || _rightTerms.get(0).getCoefficient() != 0))
            {
                transferTerm(_leftTerms.size() + i);
                i--;
            }
        }
    }

    private void transferTerm(int t)
    {
        String etape;
        Term temp;

        //Si le terme recherche est a gauche du egal.
        if (t < _leftTerms.size())
        {
            temp = new Term(_leftTerms.get(t));

            if (temp.getCoefficient() != 0)
            {
                temp.setCoefficient(-1 * temp.getCoefficient());
            }

            _leftTerms.add(new Term(temp));
            _rightTerms.add(new Term(temp));
        }
        else
        {
            temp = new Term(_rightTerms.get(t - _leftTerms.size()));

            if (temp.getCoefficient() != 0)
            {
                temp.setCoefficient(-1 * temp.getCoefficient());
            }

            _rightTerms.add(new Term(temp));
            _leftTerms.add(new Term(temp));
        }
        removeRedundantTerms(_leftTerms);
        removeRedundantTerms(_rightTerms);
        updateEquation("Transfert de terme");

        _leftTerms = sortTermsViaGroups(_leftTerms);
        _rightTerms = sortTermsViaGroups(_rightTerms);
        updateEquation("Ordonner");

        do
        {
            etape = simplify(_leftTerms);
            updateEquation(etape);
        }
        while(!etape.equals(""));

        do
        {
            etape = simplify(_rightTerms);
            updateEquation(etape);
        }
        while(!etape.equals(""));
    }

    private void solveDegreeOne()
    {
        String etape;
        Term temp;

        //Transfert du scalaire a droite.
        for (int i = 0; i < _leftTerms.size(); i++)
        {
            //Si on a un scalaire.
            if (_leftTerms.get(i).getExponent() == 0)
            {
                transferTerm(i);
            }
        }

        if (_leftTerms.get(0).getCoefficient() != 1)
        {
            //Division par le coefficient de gauche a droite.
            temp = new Term(_leftTerms.get(0));

            temp.setExponent(0);
            temp.setOperator("/");

            for (int i = 0; i < _leftTerms.size(); i++)
            {
                _leftTerms.add(i + 1, new Term(temp));
                i++;
            }

            for (int i = 0; i < _rightTerms.size(); i++)
            {
                _rightTerms.add(i + 1, new Term(temp));
                i++;
            }
            updateEquation("Transfert de terme");

            do
            {
                etape = simplify(_leftTerms);
                updateEquation(etape);
            }
            while(!etape.equals(""));

            do
            {
                etape = simplify(_rightTerms);
                updateEquation(etape);
            }
            while(!etape.equals(""));
        }
    }

    private void solveDegreeTwo()
    {
        String A = "", B = "", C = "";
        double val1, val2, a, b, c;

        for (int i = 0; i < _leftTerms.size(); i++)
        {
            if (_leftTerms.get(i).getExponent() == 2)
            {
                A = _df.format(_leftTerms.get(i).getCoefficient());
            }

            if (_leftTerms.get(i).getExponent() == 1)
            {
                B = _df.format(_leftTerms.get(i).getCoefficient());
            }

            if (_leftTerms.get(i).getExponent() == 0)
            {
                C = _df.format(_leftTerms.get(i).getCoefficient());
            }
        }

        a = !A.equals("") ? Double.parseDouble(A) : 0;
        b = !B.equals("") ? Double.parseDouble(B) : 0;
        c = !C.equals("") ? Double.parseDouble(C) : 0;

        //Si la racine est positive.
        if (Math.pow(b, 2) - (4 * a * c) >= 0)
        {
            //x1 = (-b + sqrt(b^2 - 4ac)) / 2a
            //x2 = (-b - sqrt(b^2 - 4ac)) / 2a

            val1 = (-1 * b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (a * 2);
            val2 = (-1 * b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (a * 2);

            displaySolutionsDegreeTwo(a, b, c, val1, val2);
        }
        else
        {
            System.out.println("Aucune solution reelle\n" + _variables.get(0) + " = " + _df.format((-1 * b) / (a * 2)) + " ± sqrt(" + _df.format(Math.pow(b, 2) - (4 * a * c)) + ") / " + _df.format(a * 2));
        }
    }

    private void displaySolutionsDegreeTwo(double a, double b, double c, double sol1, double sol2)
    {
        if (sol1 == sol2)
        {
            System.out.println("Solution de l'equation : \n" + _leftTerms.get(0).getCharacter() + " = " + (((-1 * b) / (a * 2)) != 0 ? _df.format((-1 * b) / (a * 2)) : 0) + " ± " + _df.format(Math.abs(Math.sqrt(Math.pow(b, 2) - (4 * a * c)))) + " / " + _df.format(a * 2) + " = " + _df.format(sol1 != 0 ? sol1 : 0) + "\n");
        }
        else
        {
            System.out.println("Solutions de l'equation : \n" + _leftTerms.get(0).getCharacter() + " = " + (((-1 * b) / (a * 2)) != 0 ? _df.format((-1 * b) / (a * 2)) : 0) + " ± " + _df.format(Math.abs(Math.sqrt(Math.pow(b, 2) - (4 * a * c)) / (a * 2))) + "\n" + _leftTerms.get(0).getCharacter() + "1 = " + _df.format(sol1 != 0 ? sol1 : 0) + " ou " + _leftTerms.get(0).getCharacter() + "2 = " + _df.format(sol2 != 0 ? sol2 : 0) + "\n");
        }
    }

    private void manageParenthesis()
    {
        String lastEquation = "", etape = "";
        ArrayList<Term> termsInParenthesis = new ArrayList<>();
        ArrayList<Term> termsTemp;
        ArrayList<Term> groupPriority = new ArrayList<>();

        //Enlever toutes les parentheses possibles.
        while(parenthesisLeft() && !lastEquation.equals(m_equation))
        {
            lastEquation = m_equation;

            //Gauche du egal.
            for (int i = 0; i < _leftTerms.size(); i++)
            {
                //Enleve les parentheses les plus proches du terme tant qu'il y en a en trop.
                while (!_leftTerms.get(i).getOpenParenthesis().isEmpty() && !_leftTerms.get(i).getCloseParenthesis().isEmpty())
                {
                    _leftTerms.get(i).setOperator(_leftTerms.get(i).getOpenParenthesis().get(0));

                    _leftTerms.get(i).getOpenParenthesis().remove(0);
                    _leftTerms.get(i).getCloseParenthesis().remove(0);
                }

                updateEquation("Enlever parentheses");

                //S'il y a une parenthese ouvrante devant le terme.
                if (!_leftTerms.get(i).getOpenParenthesis().isEmpty())
                {
                    termsInParenthesis.clear();
                    termsInParenthesis.add(new Term(_leftTerms.get(i)));
                }
                //Sinon s'il y a deja un terme entre parentheses.
                else if (!termsInParenthesis.isEmpty())
                {
                    termsInParenthesis.add(new Term(_leftTerms.get(i)));

                    //Si le terme a une parenthese fermante.
                    if (!_leftTerms.get(i).getCloseParenthesis().isEmpty())
                    {
                        termsTemp = new ArrayList<>(termsInParenthesis);
                        etape = simplify(termsInParenthesis);

                        //Si les termes entre parentheses ont pu etre simplifies.
                        if (termsTemp.size() != termsInParenthesis.size())
                        {
                            for (int j = 0; j < termsInParenthesis.size(); j++)
                            {
                                _leftTerms.add(i - 1, new Term(termsInParenthesis.get(j)));
                                _leftTerms.remove(i - j);
                            }

                            i = i - termsInParenthesis.size() - 1;
                            termsInParenthesis.clear();
                        }
                        else if (termsInParenthesis.get(0).getOpenParenthesis().get(0).equals("+"))
                        {
                            if (i + 1 <= _leftTerms.size() - 1)
                            {
                                if (!_leftTerms.get(i + 1).getOpenParenthesis().isEmpty() && _leftTerms.get(i + 1).getOpenParenthesis().get(_leftTerms.get(i + 1).getOpenParenthesis().size() - 1).equals("+") || _leftTerms.get(i + 1).getOpenParenthesis().isEmpty() && _leftTerms.get(i + 1).getOperator().equals("+"))
                                {
                                    for (int j = i - (termsInParenthesis.size() - 1); j < i + 1; j++)
                                    {
                                        _leftTerms.get(j).setOperator(_leftTerms.get(i - (termsInParenthesis.size() - 1)).getOpenParenthesis().get(0));
                                    }
                                    _leftTerms.get(i).getCloseParenthesis().remove(0);
                                    _leftTerms.get(i - (termsInParenthesis.size() - 1)).getOpenParenthesis().remove(0);

                                    termsInParenthesis.clear();
                                    etape = "Enlever parentheses";
                                }
                                else if (!_rightTerms.get(i + 1).getOpenParenthesis().isEmpty() && (_rightTerms.get(i + 1).getOpenParenthesis().get(_rightTerms.get(i + 1).getOpenParenthesis().size() - 1).equals("*") || _rightTerms.get(i + 1).getOpenParenthesis().get(_rightTerms.get(i + 1).getOpenParenthesis().size() - 1).equals("/")) || (_rightTerms.get(i + 1).getOpenParenthesis().isEmpty() && _rightTerms.get(i + 1).getOperator().equals("*") || _rightTerms.get(i + 1).getOpenParenthesis().isEmpty() && _rightTerms.get(i + 1).getOperator().equals("/")))
                                {
                                    //Si le terme suivant a au moins une parenthese ouvrante devant.
                                    if (!_leftTerms.get(i + 1).getOpenParenthesis().isEmpty())
                                    {
                                        for (int j = i + 1; j < _leftTerms.size(); j++)
                                        {
                                            groupPriority.add(new Term(_leftTerms.get(j)));

                                            if (!_leftTerms.get(j).getCloseParenthesis().isEmpty())
                                            {
                                                break;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        groupPriority.add(new Term(_leftTerms.get(i + 1)));
                                    }

                                    termsTemp = distribute(termsInParenthesis, groupPriority);
                                    etape = "Distributivite";

                                    if ((i + 1) + groupPriority.size() < _leftTerms.size())
                                    {
                                        _leftTerms.subList((i + 1) - termsInParenthesis.size(), (i + 1) + groupPriority.size()).clear();
                                    }
                                    else
                                    {
                                        _leftTerms.subList((i + 1) - termsInParenthesis.size(), _leftTerms.size()).clear();
                                    }

                                    for (int j = 0; j < termsTemp.size(); j++)
                                    {
                                        _leftTerms.add((i + 1 + j) - termsInParenthesis.size(), new Term(termsTemp.get(j)));
                                    }
                                }
                                else if (_leftTerms.get(i + 1).getOpenParenthesis().isEmpty() && _leftTerms.get(i + 1).getCoefficient() == Math.floor(_leftTerms.get(i + 1).getCoefficient()) && _leftTerms.get(i + 1).getCoefficient() > 0)
                                {
                                    //Exposants.
                                    termsTemp = new ArrayList<>(termsInParenthesis);
                                    termsTemp.get(0).getOpenParenthesis().remove(termsTemp.get(0).getOpenParenthesis().size() - 1);
                                    termsTemp.get(0).getOpenParenthesis().add(termsTemp.get(0).getOpenParenthesis().size() - 1, "*");

                                    for (int j = 0; j < _leftTerms.get(i + 1).getCoefficient() - 1; j++)
                                    {
                                        for (int k = 0; k < termsTemp.size(); k++)
                                        {
                                            _leftTerms.add((i + 1 + k + (termsTemp.size() * j)), new Term(termsTemp.get(k)));
                                        }
                                    }

                                    _leftTerms.remove(i + 1 + (termsInParenthesis.size() * (int)_leftTerms.get(i + 1).getCoefficient() - 1));
                                }
                            }
                            else
                            {
                                for (int j = i - (termsInParenthesis.size() - 1); j < i + 1; j++)
                                {
                                    _leftTerms.get(j).setOperator(_leftTerms.get(i - (termsInParenthesis.size() - 1)).getOpenParenthesis().get(0));
                                }
                                _leftTerms.get(i).getCloseParenthesis().remove(0);
                                _leftTerms.get(i - (termsInParenthesis.size() - 1)).getOpenParenthesis().remove(0);

                                termsInParenthesis.clear();
                                etape = "Enlever parentheses";
                            }
                        }
                        else
                        {
                            //Si le terme precedant a au moins une parenthese fermante apres.
                            if (!_leftTerms.get(i - termsInParenthesis.size()).getCloseParenthesis().isEmpty())
                            {
                                for (int j = i - termsInParenthesis.size(); j < 0; j--)
                                {
                                    groupPriority.add(new Term(_leftTerms.get(j)));

                                    if (!_leftTerms.get(j).getOpenParenthesis().isEmpty())
                                    {
                                        break;
                                    }
                                }

                                //On remet les termes en ordre dans groupPriority
                                for(int j = 0; j < groupPriority.size() - 1; j++)
                                {
                                    groupPriority.add(groupPriority.size() - j, new Term(groupPriority.get(j)));
                                    groupPriority.remove(j);
                                }
                            }
                            else
                            {
                                groupPriority.add(new Term(_leftTerms.get(i - termsInParenthesis.size())));
                            }

                            termsTemp = distribute(groupPriority, termsInParenthesis);
                            etape = "Distributivite";

                            if (i + 1 < _leftTerms.size())
                            {
                                _leftTerms.subList((i + 1) - groupPriority.size() - termsInParenthesis.size(), i + 1).clear();
                            }
                            else
                            {
                                _leftTerms.subList((i + 1) - groupPriority.size() - termsInParenthesis.size(), _leftTerms.size()).clear();
                            }

                            for (int j = 0; j < termsTemp.size(); j++)
                            {
                                _leftTerms.add((i + 1 + j) - groupPriority.size() - termsInParenthesis.size(), new Term(termsTemp.get(j)));
                            }
                        }
                    }
                }

                updateEquation(etape);
            }

            //Droite du egal.
            for (int i = 0; i < _rightTerms.size(); i++)
            {
                //Enleve les parentheses les plus proches du terme tant qu'il y en a en trop.
                while (!_rightTerms.get(i).getOpenParenthesis().isEmpty() && !_rightTerms.get(i).getCloseParenthesis().isEmpty())
                {
                    _rightTerms.get(i).setOperator(_rightTerms.get(i).getOpenParenthesis().get(0));

                    _rightTerms.get(i).getOpenParenthesis().remove(0);
                    _rightTerms.get(i).getCloseParenthesis().remove(0);
                }

                updateEquation("Enlever parentheses");

                //S'il y a une parenthese ouvrante devant le terme.
                if (!_rightTerms.get(i).getOpenParenthesis().isEmpty())
                {
                    termsInParenthesis.clear();
                    termsInParenthesis.add(new Term(_rightTerms.get(i)));
                }
                //Sinon s'il y a deja un terme entre parentheses.
                else if (!termsInParenthesis.isEmpty())
                {
                    termsInParenthesis.add(new Term(_rightTerms.get(i)));

                    //Si le terme a une parenthese fermante.
                    if (!_rightTerms.get(i).getCloseParenthesis().isEmpty())
                    {
                        termsTemp = new ArrayList<>(termsInParenthesis);
                        etape = simplify(termsInParenthesis);

                        //Si les termes entre parentheses ont pu etre simplifies.
                        if (termsTemp.size() != termsInParenthesis.size())
                        {
                            for (int j = 0; j < termsInParenthesis.size(); j++)
                            {
                                _rightTerms.add(i - 1, new Term(termsInParenthesis.get(j)));
                                _rightTerms.remove(i - j);
                            }

                            i = i - termsInParenthesis.size() - 1;
                            termsInParenthesis.clear();
                        }
                        else if (termsInParenthesis.get(0).getOpenParenthesis().get(0).equals("+"))
                        {
                            if (i + 1 <= _rightTerms.size() - 1)
                            {
                                if (!_rightTerms.get(i + 1).getOpenParenthesis().isEmpty() && _rightTerms.get(i + 1).getOpenParenthesis().get(_rightTerms.get(i + 1).getOpenParenthesis().size() - 1).equals("+") || _rightTerms.get(i + 1).getOpenParenthesis().isEmpty() && _rightTerms.get(i + 1).getOperator().equals("+"))
                                {
                                    for (int j = i - (termsInParenthesis.size() - 1); j < i + 1; j++)
                                    {
                                        _rightTerms.get(j).setOperator(_rightTerms.get(i - (termsInParenthesis.size() - 1)).getOpenParenthesis().get(0));
                                    }
                                    _rightTerms.get(i).getCloseParenthesis().remove(0);
                                    _rightTerms.get(i - (termsInParenthesis.size() - 1)).getOpenParenthesis().remove(0);

                                    termsInParenthesis.clear();
                                    etape = "Enlever parentheses";
                                }
                                else if (!_rightTerms.get(i + 1).getOpenParenthesis().isEmpty() && (_rightTerms.get(i + 1).getOpenParenthesis().get(_rightTerms.get(i + 1).getOpenParenthesis().size() - 1).equals("*") || _rightTerms.get(i + 1).getOpenParenthesis().get(_rightTerms.get(i + 1).getOpenParenthesis().size() - 1).equals("/")) || (_rightTerms.get(i + 1).getOpenParenthesis().isEmpty() && _rightTerms.get(i + 1).getOperator().equals("*") || _rightTerms.get(i + 1).getOpenParenthesis().isEmpty() && _rightTerms.get(i + 1).getOperator().equals("/")))
                                {
                                    //Si le terme suivant a au moins une parenthese ouvrante devant.
                                    if (!_rightTerms.get(i + 1).getOpenParenthesis().isEmpty())
                                    {
                                        for (int j = i + 1; j < _rightTerms.size(); j++)
                                        {
                                            groupPriority.add(new Term(_rightTerms.get(j)));

                                            if (!_rightTerms.get(j).getCloseParenthesis().isEmpty())
                                            {
                                                break;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        groupPriority.add(new Term(_rightTerms.get(i + 1)));
                                    }

                                    termsTemp = distribute(termsInParenthesis, groupPriority);
                                    etape = "Distributivite";

                                    if ((i + 1) + groupPriority.size() < _rightTerms.size())
                                    {
                                        _rightTerms.subList((i + 1) - termsInParenthesis.size(), (i + 1) + groupPriority.size()).clear();
                                    }
                                    else
                                    {
                                        _rightTerms.subList((i + 1) - termsInParenthesis.size(), _rightTerms.size()).clear();
                                    }

                                    for (int j = 0; j < termsTemp.size(); j++)
                                    {
                                        _rightTerms.add((i + 1 + j) - termsInParenthesis.size(), new Term(termsTemp.get(j)));
                                    }
                                }
                                else if (_rightTerms.get(i + 1).getOpenParenthesis().isEmpty() && _rightTerms.get(i + 1).getCoefficient() == Math.floor(_rightTerms.get(i + 1).getCoefficient()) && _rightTerms.get(i + 1).getCoefficient() > 0)
                                {
                                    //Exposants.
                                    termsInParenthesis.get(0).getOpenParenthesis().remove(termsInParenthesis.get(0).getOpenParenthesis().size() - 1);
                                    termsInParenthesis.get(0).getOpenParenthesis().add("*");

                                    for (int j = 0; j < _rightTerms.get(i + 1).getCoefficient() - 1; j++)
                                    {
                                        _rightTerms.addAll((i + 1 + (termsTemp.size() * j)), new ArrayList<>(termsInParenthesis));
                                    }

                                    _rightTerms.remove(i + 1 + (termsInParenthesis.size() * (int)_rightTerms.get(i + 1).getCoefficient()));

                                    etape = "Exposant";
                                }
                            }
                            else
                            {
                                for (int j = i - (termsInParenthesis.size() - 1); j < i + 1; j++)
                                {
                                    _rightTerms.get(j).setOperator(_rightTerms.get(i - (termsInParenthesis.size() - 1)).getOpenParenthesis().get(0));
                                }
                                _rightTerms.get(i).getCloseParenthesis().remove(0);
                                _rightTerms.get(i - (termsInParenthesis.size() - 1)).getOpenParenthesis().remove(0);

                                termsInParenthesis.clear();
                                etape = "Enlever parentheses";
                            }
                        }
                        else
                        {
                            //Si le terme precedant a au moins une parenthese fermante apres.
                            if (!_rightTerms.get(i - termsInParenthesis.size()).getCloseParenthesis().isEmpty())
                            {
                                for (int j = i - termsInParenthesis.size(); j >= 0; j--)
                                {
                                    groupPriority.add(new Term(_rightTerms.get(j)));

                                    if (!_rightTerms.get(j).getOpenParenthesis().isEmpty())
                                    {
                                        break;
                                    }
                                }

                                //On remet les termes en ordre dans groupPriority
                                for(int j = 0; j < groupPriority.size() - 1; j++)
                                {
                                    groupPriority.add(groupPriority.size() - j, new Term(groupPriority.get(j)));
                                    groupPriority.remove(j);
                                }
                            }
                            else
                            {
                                groupPriority.add(new Term(_rightTerms.get(i - termsInParenthesis.size())));
                            }

                            termsTemp = distribute(groupPriority, termsInParenthesis);
                            etape = "Distributivite";

                            if (i + 1 < _rightTerms.size())
                            {
                                _rightTerms.subList((i + 1) - groupPriority.size() - termsInParenthesis.size(), i + 1).clear();
                            }
                            else
                            {
                                _rightTerms.subList((i + 1) - groupPriority.size() - termsInParenthesis.size(), _rightTerms.size()).clear();
                            }

                            for (int j = 0; j < termsTemp.size(); j++)
                            {
                                _rightTerms.add((i + 1 + j) - groupPriority.size() - termsInParenthesis.size(), new Term(termsTemp.get(j)));
                            }
                        }
                    }
                }

                updateEquation(etape);
            }
        }
    }

    private boolean parenthesisLeft()
    {
        for (Term term : _leftTerms)
        {
            if (!term.getOpenParenthesis().isEmpty())
            {
                return true;
            }
        }

        for (Term term : _rightTerms)
        {
            if (!term.getOpenParenthesis().isEmpty())
            {
                return true;
            }
        }

        return false;
    }

    private ArrayList<Term> distribute(ArrayList<Term> terms1, ArrayList<Term> terms2)
    {
        ArrayList<ArrayList<Term>> groups = new ArrayList<>();
        ArrayList<Term> temp = new ArrayList<>();

        if (!terms1.get(0).getOpenParenthesis().isEmpty() && !terms1.get(terms1.size() - 1).getCloseParenthesis().isEmpty())
        {
            for (int i = 0; i < terms1.size(); i++)
            {
                terms1.get(i).setOperator(terms1.get(0).getOpenParenthesis().get(terms1.get(0).getOpenParenthesis().size() - 1));
            }

            terms1.get(0).getOpenParenthesis().remove(terms1.get(0).getOpenParenthesis().size() - 1);
            terms1.get(terms1.size() - 1).getCloseParenthesis().remove(terms1.get(terms1.size() - 1).getCloseParenthesis().size() - 1);
        }

        if (!terms2.get(0).getOpenParenthesis().isEmpty() && !terms2.get(terms2.size() - 1).getCloseParenthesis().isEmpty())
        {
            for (int i = 0; i < terms2.size(); i++)
            {
                terms2.get(i).setOperator(terms2.get(0).getOpenParenthesis().get(terms2.get(0).getOpenParenthesis().size() - 1));
            }

            terms2.get(0).getOpenParenthesis().remove(terms2.get(0).getOpenParenthesis().size() - 1);
            terms2.get(terms2.size() - 1).getCloseParenthesis().remove(terms2.get(terms2.size() - 1).getCloseParenthesis().size() - 1);
        }

        for (int i = 0; i < terms1.size(); i++)
        {
            for (int j = 0; j < terms2.size(); j++)
            {
                temp.add(new Term(terms1.get(i)));
                temp.add(new Term(terms2.get(j)));

                groups.add(new ArrayList<>(temp));
                temp.clear();
            }
        }

        for (ArrayList<Term> terms : groups)
        {
            for (Term term : terms)
            {
                temp.add(new Term(term));
            }
        }

        return temp;
    }

    private void updateEquation(String etape)
    {
        String lastEquation = m_equation;
        m_equation = "";                                         //Efface l'equation

        //Gauche du egal.
        int index = 0;
        while (_leftTerms.size() > index)			//S'il y a un prochain terme.
        {
            m_equation += _leftTerms.get(index).display();       //Affiche le prochain terme et son operateur devant.
            index++;
        }

        m_equation += "=";					//Rajoute le egal.

        //Droite du egal.
        index = 0;
        while (_rightTerms.size() > index)			//S'il y a un prochain terme.
        {
            m_equation += _rightTerms.get(index).display();      //Affiche le prochain terme et son operateur devant.
            index++;
        }

        //Affichage de l'equation.
        if (!m_equation.equals(lastEquation) || etape.equals("Equation recue"))
        {
            displayEquation(etape);
        }
    }

    private void displayEquation(String etape)
    {
        String equation = "";

        //Gauche du egal.
        for (int i = 0; i < _leftTerms.size(); i++)
        {
            //S'il y a une parenthese ouvrante avant le terme.
            if (!_leftTerms.get(i).getOpenParenthesis().isEmpty())
            {
                for (String open : _leftTerms.get(i).getOpenParenthesis())
                {
                    equation += (i != 0 ? (" " + open + " ") : "") + "(";
                }
            }

            if (i != 0 && equation.charAt(equation.length() - 1) != '(')
            {
                equation += " ";

                if (_leftTerms.get(i).getOperator().equals("+") && _leftTerms.get(i).getCoefficient() < 0)
                {
                    equation += "- ";
                }
                else if (_leftTerms.get(i).getOperator().equals("+"))
                {
                    equation += _leftTerms.get(i).getOperator() + " ";
                }
                else if (_leftTerms.get(i).getCoefficient() < 0)
                {
                    equation += _leftTerms.get(i).getOperator() + " -";
                }
                else
                {
                    equation += _leftTerms.get(i).getOperator() + " ";
                }
            }
            else
            {
                if (_leftTerms.get(i).getOperator().equals("+") && _leftTerms.get(i).getCoefficient() < 0)
                {
                    equation += "-";
                }
                else if (!_leftTerms.get(i).getOperator().equals("+"))
                {
                    equation += _leftTerms.get(i).getOperator();
                }
            }

            if (Math.abs(_leftTerms.get(i).getExponent()) == 0 || Math.abs(_leftTerms.get(i).getCoefficient()) != 1)
            {
                equation += _df.format(Math.abs(_leftTerms.get(i).getCoefficient()));
            }

            if (Math.abs(_leftTerms.get(i).getExponent()) != 0 && Math.abs(_leftTerms.get(i).getCoefficient()) != 0)
            {
                equation += _leftTerms.get(i).getCharacter();
            }

            if (Math.abs(_leftTerms.get(i).getExponent()) != 1 && Math.abs(_leftTerms.get(i).getExponent()) != 0 && Math.abs(_leftTerms.get(i).getCoefficient()) != 0)
            {
                equation += "^";
                equation += _df.format(_leftTerms.get(i).getExponent());
            }

            //S'il y a une parenthese fermante apres le terme.
            if (!_leftTerms.get(i).getCloseParenthesis().isEmpty())
            {
                for (String close : _leftTerms.get(i).getCloseParenthesis())
                {
                    equation += ")";
                }
            }
        }

        equation += " = ";

        //Droite du egal.
        for (int i = 0; i < _rightTerms.size(); i++)
        {
            //S'il y a une parenthese ouvrante avant le terme.
            if (!_rightTerms.get(i).getOpenParenthesis().isEmpty())
            {
                for (String open : _rightTerms.get(i).getOpenParenthesis())
                {
                    equation += (i != 0 ? (" " + open + " ") : "") + "(";
                }
            }

            if (i != 0 && equation.charAt(equation.length() - 1) != '(')
            {
                equation += " ";

                if (_rightTerms.get(i).getOperator().equals("+") && _rightTerms.get(i).getCoefficient() < 0)
                {
                    equation += "- ";
                }
                else if (_rightTerms.get(i).getOperator().equals("+"))
                {
                    equation += _rightTerms.get(i).getOperator() + " ";
                }
                else if (_rightTerms.get(i).getCoefficient() < 0)
                {
                    equation += _rightTerms.get(i).getOperator() + " -";
                }
                else
                {
                    equation += _rightTerms.get(i).getOperator() + " ";
                }
            }
            else
            {
                if (_rightTerms.get(i).getOperator().equals("+") && _rightTerms.get(i).getCoefficient() < 0)
                {
                    equation += "-";
                }
                else if (!_rightTerms.get(i).getOperator().equals("+"))
                {
                    equation += _rightTerms.get(i).getOperator();
                }
            }

            if ((Math.abs(_rightTerms.get(i).getExponent())) == 0 || Math.abs(_rightTerms.get(i).getCoefficient()) != 1)
            {
                equation += _df.format(Math.abs(_rightTerms.get(i).getCoefficient()));
            }

            if (Math.abs(_rightTerms.get(i).getExponent()) != 0 && Math.abs(_rightTerms.get(i).getCoefficient()) != 0)
            {
                equation += _rightTerms.get(i).getCharacter();
            }

            if (Math.abs(_rightTerms.get(i).getExponent()) != 1 && Math.abs(_rightTerms.get(i).getExponent()) != 0 && Math.abs(_rightTerms.get(i).getCoefficient()) != 0)
            {
                equation += "^";
                equation += _df.format(_rightTerms.get(i).getExponent());
            }

            //S'il y a une parenthese fermante apres le terme.
            if (!_rightTerms.get(i).getCloseParenthesis().isEmpty())
            {
                for (String close : _rightTerms.get(i).getCloseParenthesis())
                {
                    equation += ")";
                }
            }
        }

        //Pour l'alignement.
        if (_equationLegthDisplay == 0)
        {
            _equationLegthDisplay = equation.length();
        }

        m_DemarcheText.add(equation);
        m_EtapesText.add(etape);
        System.out.print(equation + "(" + etape + ")");
    }

}

//DecimalFormat: http://stackoverflow.com/questions/14204905/java-how-to-remove-trailing-zeros-from-a-double