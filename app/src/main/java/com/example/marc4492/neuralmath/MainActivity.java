package com.example.marc4492.neuralmath;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static Context context;

    //Info screen
    private int largeurScreen;

    private ViewFlipper activity_main;

    private TextView layoutOptionsText;
    private TextView languageOptionsText;
    private TextView defaultModeOptionsText;
    private TextView feuilleOptionsText;

    private RadioGroup layoutOption;
    private RadioGroup langueOption;
    private RadioGroup defaultOption;
    private RadioGroup feuilleOption;


    private MathKeyboard mathKeyboard;
    private MathEditText writingZone;

    //Preferences
    private SharedPreferences sharedPrefs;

    private static boolean isDroitier;
    private String defautMode;
    private static boolean isBlankPage;
    private String langue;


    //Image decoder and NeuralNetwork
    private static ImageDecoder imageDecoder;

    private final static int INPUT = 784;
    private final static int HIDDEN = 100;
    private final static int OUTPUT = 10;
    private final static double TRAININGRATE = 0.005;

    private final String tableNameItoH = "weights_i_to_h";
    private final String tableNameHtoO = "weights_h_to_o";

    private SQLiteDatabase database;

    private static boolean checkPrefDefault = true;

    private String[] charListComplete =
            {
                    "!", "(", ")", "+", ",", "-", "0", "1", "2", "3", "4",
                    "5", "6", "7", "8", "9", "=", "a", "α", "//d",
                    "b", "β", "c", "cos", "d", "Δ", "÷", "/e/",
                    "f", "/", "g", "γ", "≥", ">", "h", "i",
                    "∞", "//S", "j", "k", "l", "λ", "≤", "lim",
                    "log", "<", "m", "μ", "n", "≠", "o", "p",
                    "ϕ", "/PI/", "±", "*", "'", "q", "r", "→",
                    "s", "σ", "sin", "√", "Σ", "t", "tan", "θ",
                    "u", "v", "w", "x", "y", "z", "[", "]", "{", "}"
            };

    private String[] charListSimple =
            {
                    "0", "1", "2", "3", "4",
                    "5", "6", "7", "8", "9"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        getScreenSize();

        database = openOrCreateDatabase("BDNM", MODE_PRIVATE, null);

        //Main view
        // getting all widget
        activity_main = (ViewFlipper) findViewById(R.id.activity_main);
        ListView listHome = (ListView) findViewById(R.id.listHome);

        //TextView
        layoutOptionsText = (TextView) findViewById(R.id.layoutOptionsText);
        languageOptionsText = (TextView) findViewById(R.id.languageOptionsText);
        defaultModeOptionsText = (TextView) findViewById(R.id.defaultModeOptionsText);
        feuilleOptionsText = (TextView) findViewById(R.id.feuilleTypeOptionText);

        //RadioGroup
        layoutOption = (RadioGroup) findViewById(R.id.layoutOption);
        langueOption = (RadioGroup) findViewById(R.id.langueOption);
        defaultOption = (RadioGroup) findViewById(R.id.defautOption);
        feuilleOption = (RadioGroup) findViewById(R.id.feuilleTypeOption);

        writingZone = (MathEditText) findViewById(R.id.writingZone);

        mathKeyboard = (MathKeyboard) findViewById(R.id.keyboard);
        mathKeyboard.setListener(new MathKeyboard.OnStringReadyListener() {
            @Override
            public void done(String value) {
                if(!value.equals("")) {
                    try {
                        imageDecoder.trainNN(mathKeyboard.getReplacedCharList());
                    } catch (IOException ex) {
                        Log.e("NeuralNetwork", "Training", ex);
                    }
                    Intent i = new Intent(context, ProcedureResolutionEquation.class);
                    i.putExtra("EQUATION", value);
                    startActivity(i);
                    onBackPressed();
                    writingZone.setText("");
                }
            }
        });

        ArrayList<HomeRow> homeRows = new ArrayList<>();

        //Getting the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;


        //adding home element
        homeRows.add(new HomeRow(this, R.drawable.camera, getResources().getString(R.string.photo)));
        homeRows.add(new HomeRow(this, R.drawable.pen, getResources().getString(R.string.ecrire)));
        homeRows.add(new HomeRow(this, R.drawable.keyboard, getResources().getString(R.string.clavier)));
        homeRows.add(new HomeRow(this, R.drawable.wrench, getResources().getString(R.string.parametres)));

        AdapterHome adapterHome = new AdapterHome(MainActivity.this, R.layout.menu_elements_layout, homeRows);

        listHome.setAdapter(adapterHome);


        //handling click events for the home page items
        listHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection
                switch (position) {
                    case 0:         //Photo element selected

                        //TODO
                        //enlever les commentaires sur les if

                        if(imageDecoder.isReady())
                            openPhoto();
                        else
                            Toast.makeText(context, R.string.nn_not_ready, Toast.LENGTH_LONG).show();
                        break;
                    case 1:         //Writing element selected
                        if(imageDecoder.isReady())
                            openWriting();
                        else
                            Toast.makeText(context, R.string.nn_not_ready, Toast.LENGTH_LONG).show();
                        break;
                    case 2:         //Keyboard element selected
                        openKeyboard();
                        break;
                    case 3:         //Parameter element selected
                        openParameter();
                        break;
                }
            }
        });

        adjustTextToScreen(screenHeight);

        //Préférence
        sharedPrefs = getPreferences(Context.MODE_PRIVATE);

        //on crée les préférences si elles n'existent pas
        if (!sharedPrefs.contains("layout") && !sharedPrefs.contains("langue") && !sharedPrefs.contains("default") || !sharedPrefs.contains("feuille")) {
            try {
                ((RadioButton) layoutOption.getChildAt(0)).setChecked(true);
                ((RadioButton) langueOption.getChildAt(0)).setChecked(true);
                ((RadioButton) defaultOption.getChildAt(0)).setChecked(true);
                ((RadioButton) feuilleOption.getChildAt(0)).setChecked(true);

                firstTimeOnApp();
            } catch (IOException ex) {
                Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            getPref();

            if (checkPrefDefault) {
                checkPrefDefault = false;
                if (defautMode.equals(getResources().getStringArray(R.array.default_page)[0]))
                    openHome();
                else if (defautMode.equals(getResources().getStringArray(R.array.default_page)[1]))
                    openPhoto();
                else if (defautMode.equals(getResources().getStringArray(R.array.default_page)[2]))
                    openWriting();
                else if (defautMode.equals(getResources().getStringArray(R.array.default_page)[3]))
                    openKeyboard();
                else
                    openHome();
            }

            try {
                createNetworkDecoder();
            } catch (IOException ex) {
                Toast.makeText(context, R.string.network_accessibility_error, Toast.LENGTH_SHORT).show();
            }
        }


        /* ------------------------------------------------
         * code from: http://stackoverflow.com/a/13975236
         * author: Eddie Sullivan
         * consulted date: 22 March 2017
         */
        // Update the EditText so it won't popup Android's own keyboard, since I have my own.
        writingZone.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });
        //--------------------------------------------------

        writingZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mathKeyboard.openKeyboard(writingZone, largeurScreen);
            }
        });


    }

    private void createNetworkDecoder() throws IOException {
        Toast.makeText(context, "Waiting for neural network", Toast.LENGTH_LONG).show();
        imageDecoder = new ImageDecoder(context, INPUT, HIDDEN, OUTPUT, TRAININGRATE, database, charListSimple);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * handling click events for the main menu items
     *
     * @param item L'Item pressed
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mItemParamètres:
                openParameter();
                return true;
            case R.id.mItemHome:
                openHome();
                return true;
            case R.id.mItemPhoto:
                openPhoto();
                return true;
            case R.id.mItemEcrire:
                openWriting();
                return true;
            case R.id.mItemClavier:
                openKeyboard();
                return true;
            case R.id.mItemAbout:
                openAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (activity_main.getDisplayedChild() == 1) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            //Ajouter les nouvelle valeur dans les pref

            int indexLayout = layoutOption.indexOfChild(layoutOption.findViewById(layoutOption.getCheckedRadioButtonId()));
            int indexFeuille = feuilleOption.indexOfChild(feuilleOption.findViewById(feuilleOption.getCheckedRadioButtonId()));
            int indexLangue = langueOption.indexOfChild(langueOption.findViewById(langueOption.getCheckedRadioButtonId()));
            int indexDefault = defaultOption.indexOfChild(defaultOption.findViewById(defaultOption.getCheckedRadioButtonId()));

            editor.putString("layout", getResources().getStringArray(R.array.layout)[indexLayout]);
            editor.putString("feuille", getResources().getStringArray(R.array.type_feuille)[indexFeuille]);
            editor.putString("langue", getResources().getStringArray(R.array.langue)[indexLangue]);
            editor.putString("default", getResources().getStringArray(R.array.default_page)[indexDefault]);

            editor.apply();
            getPref();
            openHome();
        } else if (activity_main.getDisplayedChild() != 0)
            openHome();
        else
            super.onBackPressed();
    }

    //http://stackoverflow.com/a/14292451/5224674
    //Pour recevoir une string d'une autre activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            final String eq = data.getStringExtra("EQUATION");

            if (requestCode == 1) {
                if (resultCode == RESULT_OK && !eq.equals("")) {

                    new AlertDialog.Builder(context)
                            .setTitle(R.string.confirmation)
                            .setMessage(getString(R.string.your_eq_confirm) + " " + eq + " ?")
                            .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(context, ProcedureResolutionEquation.class);
                                    i.putExtra("EQUATION", eq);
                                    startActivity(i);
                                }
                            })
                            .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    correctionModePopup(eq);
                                }
                            })
                            .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                }
            }
        }
    }

    /**
     * Get l'image decoder pour les autres intent
     * @return      L'image decoder
     */
    public static ImageDecoder getImageDecoder() {
        return imageDecoder;
    }

    /**
     * Get le context pour les autres intents
     * @return      Le context de l'app
     */
    public static Context getContext()
    {
        return context;
    }

    /**
     * Afficher le popup pour le type de correction à effectuer
     *
     * @param eq    L'equation à corriger
     */
    private void correctionModePopup(final String eq)
    {
        new AlertDialog.Builder(context)
                .setTitle(R.string.type_correction)
                .setMessage(getString(R.string.your_eq_confirm) + " " + eq + " ?")
                .setNegativeButton(R.string.corriger, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mathKeyboard.setCorrectionMode(true);
                        writingZone.getText().clear();
                        writingZone.setText(eq);
                        activity_main.setDisplayedChild(2);
                    }
                })
                .setPositiveButton(R.string.editer, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mathKeyboard.setCorrectionMode(false);
                        writingZone.getText().clear();
                        writingZone.setText(eq);
                        activity_main.setDisplayedChild(2);
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }

    /**
     * Get largueur et hauteur de la fenêtre
     */
    void getScreenSize() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        largeurScreen = displaymetrics.widthPixels;
    }

    /**
     * @throws IOException           Problème de lecture de fichier
     * @throws NumberFormatException Mauvais format de données
     */
    private void firstTimeOnApp() throws IOException, NumberFormatException {
        Toast.makeText(context, R.string.welcome, Toast.LENGTH_LONG).show();

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.setTitle(getString(R.string.transfer_fichier_1));
        progress.setMessage(getString(R.string.transfere_seul_fois));
        progress.setProgress(0);
        progress.setMax(INPUT);
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    readFileAndTransferDB(getResources().openRawResource(R.raw.weights_i_to_h), tableNameItoH, progress);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setTitle(getString(R.string.transfer_fichier_2));
                            progress.setProgress(0);
                            progress.setMax(HIDDEN);
                        }
                    });
                    readFileAndTransferDB(getResources().openRawResource(R.raw.weights_h_to_o), tableNameHtoO, progress);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });

                    createNetworkDecoder();
                } catch (IOException ex) {
                    Toast.makeText(context, R.string.txt_file_error, Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

        activity_main.setDisplayedChild(1);
    }

    /**
     * Lecture d'un tableau deux dimension depuis un fichier texte.
     *
     * @param inputStream L'inputStream venant des ressources
     * @param nameTable   Nom de la table à écrire dans la DB
     * @throws IOException           S'il y a des problème de lecture dans le fichier ou que le fichier n'a pas les bonnes tailles. (nbs lignes/colonnes)
     * @throws NumberFormatException Si le texte n'est pas en double
     */
    private void readFileAndTransferDB(InputStream inputStream, String nameTable, ProgressDialog progress) throws IOException, NumberFormatException {
        database.execSQL("DROP TABLE IF EXISTS " + nameTable);
        database.execSQL("CREATE TABLE " + nameTable + "(valeur DOUBLE)");

        //From
        //http://stackoverflow.com/a/19637484
        String query = "INSERT INTO " + nameTable + " (valeur) values (?);";
        String line;
        String[] lineItems;

        //Début de l'ecriture dans la DB
        database.beginTransaction();
        SQLiteStatement stmt = database.compileStatement(query);

        //Facon efficace de lire dans un fichier texte
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(isr);

        int progressVal = 0;

        //Lecture jusqu'à la fin du fichier
        while ((line = reader.readLine()) != null) {
            //Split les ", "
            lineItems = line.substring(1, line.length() - 1).split("\\s*,\\s*");

            //Save dans la DB
            for (String value : lineItems) {
                stmt.bindDouble(1, Double.parseDouble(value));
                stmt.executeInsert();
                //stmt.clearBindings();
            }

            progress.setProgress(++progressVal);
        }

        reader.close();
        isr.close();

        database.setTransactionSuccessful();
        database.endTransaction();
    }


    /**
     * Open the photo mode page
     */
    void openPhoto() {
        Intent i = new Intent(context, CameraActivity.class);
        i.putExtra("FEUILLE", isBlankPage);
        i.putExtra("LANGUE", langue);
        startActivityForResult(i, 1);
    }

    /**
     * Open Writing page
     */
    void openWriting() {
        //New Intent pour gerer la screen orientation
        Intent i = new Intent(context, DrawingActivity.class);
        i.putExtra("LAYOUT", String.valueOf(isDroitier));
        i.putExtra("LANGUE", langue);
        startActivityForResult(i, 1);
    }

    /**
     * Open Keyboard page
     */
    void openKeyboard() {
        activity_main.setDisplayedChild(2);
        mathKeyboard.setCorrectionMode(false);
    }

    /**
     * open parameter page
     */
    void openParameter() {
        activity_main.setDisplayedChild(1); //the parameter page is 1
    }

    /**
     * open home page
     */
    void openHome() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activity_main.setDisplayedChild(0); //the home page is 0
    }

    /**
     * open about dialog
     */
    void openAbout() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.propos)
                .setMessage(R.string.aboutTxt)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


    /**
     * Adjust the text size depending on the screen size
     *
     * @param height the height of the screen
     */
    void adjustTextToScreen(int height) {
        int textViewSize = height / 45;
        int radioBtnTxtSize = height / 65;
        layoutOptionsText.setTextSize(textViewSize);
        languageOptionsText.setTextSize(textViewSize);
        defaultModeOptionsText.setTextSize(textViewSize);
        feuilleOptionsText.setTextSize(textViewSize);


        for (int i = 0; i < layoutOption.getChildCount(); i++)
            ((RadioButton) layoutOption.getChildAt(i)).setTextSize(radioBtnTxtSize);

        for (int i = 0; i < langueOption.getChildCount(); i++)
            ((RadioButton) langueOption.getChildAt(i)).setTextSize(radioBtnTxtSize);

        for (int i = 0; i < defaultOption.getChildCount(); i++)
            ((RadioButton) defaultOption.getChildAt(i)).setTextSize(radioBtnTxtSize);

        for (int i = 0; i < feuilleOption.getChildCount(); i++)
            ((RadioButton) feuilleOption.getChildAt(i)).setTextSize(radioBtnTxtSize);
    }


    /**
     * Lire les preférence du user
     */
    private void getPref() {
        //set les variables de préférences******************************************************************
        langue = sharedPrefs.getString("langue", getResources().getStringArray(R.array.langue)[0]);
        defautMode = sharedPrefs.getString("default", getResources().getStringArray(R.array.langue)[0]);

        int indexLayout = Arrays.asList((getResources().getStringArray(R.array.layout))).indexOf(sharedPrefs.getString("layout", getResources().getStringArray(R.array.langue)[0]));
        int indexFeuille = Arrays.asList((getResources().getStringArray(R.array.type_feuille))).indexOf(sharedPrefs.getString("feuille", getResources().getStringArray(R.array.langue)[0]));
        int indexLangue = Arrays.asList((getResources().getStringArray(R.array.langue))).indexOf(langue);
        int indexDefault = Arrays.asList((getResources().getStringArray(R.array.default_page))).indexOf(defautMode);

        ((RadioButton) layoutOption.getChildAt(indexLayout)).setChecked(true);
        ((RadioButton) feuilleOption.getChildAt(indexFeuille)).setChecked(true);
        ((RadioButton) langueOption.getChildAt(indexLangue)).setChecked(true);
        ((RadioButton) defaultOption.getChildAt(indexDefault)).setChecked(true);


        isDroitier = indexLayout == 0;
        isBlankPage = indexFeuille == 0;

        if (langue.equals(getResources().getStringArray(R.array.langue)[0])) {
            langue = "fr";
        } else if (langue.equals(getResources().getStringArray(R.array.langue)[1])) {
            langue = "en";
        }

        changementDeLangue(langue, this);
    }

    /**
     * Changement de lanque
     *
     * @param languageToLoad Langue à afficher
     */
    public static void changementDeLangue(String languageToLoad, AppCompatActivity activity) {
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        String currentLocale = getContext().getResources().getConfiguration().locale.toString();
        currentLocale = currentLocale.substring(0, 2);
        String newLocale = locale.toString();
        newLocale = newLocale.substring(0, 2);
        if (!newLocale.equals(currentLocale)) {
            Configuration config = new Configuration();
            config.locale = locale;
            getContext().getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());


            activity.finish();
            Intent intent = new Intent(context, activity.getClass());
            intent.putExtra("LANGUE", newLocale);

            if(activity instanceof DrawingActivity)
                intent.putExtra("LAYOUT", String.valueOf(isDroitier));
            else if(activity instanceof CameraActivity)
                intent.putExtra("FEUILLE", isBlankPage);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        }
    }

    public void confirmPref(View view) {
        onBackPressed();
    }
}