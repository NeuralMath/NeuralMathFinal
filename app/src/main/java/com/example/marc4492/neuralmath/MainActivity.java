package com.example.marc4492.neuralmath;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context context;




    private ListView listHome;
    private ArrayList<HomeRow> homeRows;
    private ViewFlipper activity_main;

    private TextView layoutOptionsText;
    private TextView languageOptionsText;
    private TextView defaultModeOptionsText;

    private RadioGroup layoutOption;
    private RadioGroup langueOption;
    private RadioGroup defautOption;

    /*private RadioButton leftOption;
    private RadioButton rightOption;

    private RadioButton frenchOption;
    private RadioButton englishOption;

    private RadioButton homeOption;
    private RadioButton photoOption;
    private RadioButton writeOption;
    private RadioButton menuOption;*/

    private int screenWidth, screenHeight;


    //Préférences
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPrefs;

    private boolean isDroitier;
    private String langue;
    private String defautMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        //getting all widget
        activity_main = (ViewFlipper) findViewById(R.id.activity_main);
        listHome  = (ListView) findViewById(R.id.listHome);


        //TextView
        layoutOptionsText = (TextView) findViewById(R.id.layoutOptionsText);
        languageOptionsText = (TextView) findViewById(R.id.languageOptionsText);
        defaultModeOptionsText = (TextView) findViewById(R.id.defaultModeOptionsText);

        //RadioGroup
        layoutOption = (RadioGroup) findViewById(R.id.layoutOption);
        langueOption = (RadioGroup) findViewById(R.id.langueOption);
        defautOption = (RadioGroup) findViewById(R.id.defautOption);

        //RadioButton
        /*leftOption = (RadioButton) findViewById(R.id.leftOption);
        rightOption = (RadioButton) findViewById(R.id.rightOption);
        frenchOption = (RadioButton) findViewById(R.id.frenchOption);
        englishOption = (RadioButton) findViewById(R.id.englishOption);
        homeOption = (RadioButton) findViewById(R.id.homeOption);
        photoOption = (RadioButton) findViewById(R.id.photoOption);
        writeOption = (RadioButton) findViewById(R.id.writeOption);
        menuOption = (RadioButton) findViewById(R.id.menuOption);*/

        homeRows = new ArrayList<>();

        //Getting the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        //adding home element
        homeRows.add(new HomeRow(R.drawable.camera, getResources().getString(R.string.photo)));
        homeRows.add(new HomeRow(R.drawable.pen, getResources().getString(R.string.ecrire)));
        homeRows.add(new HomeRow(R.drawable.keyboard, getResources().getString(R.string.clavier)));
        homeRows.add(new HomeRow(R.drawable.wrench, getResources().getString(R.string.parametres)));

        AdapterHome adapterHome = new AdapterHome(MainActivity.this, R.layout.menu_elements_layout, homeRows);

        listHome.setAdapter(adapterHome);



        //handling click events for the home page items
        listHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection
                switch (position) {
                    case 0:         //Photo element selected
                        openPhoto();
                        break;
                    case 1:         //Writing element selected
                        openWriting();
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
        editor = sharedPrefs.edit();

        //on crée les préférences si elles n'existe pas
        //préférence pour la playlist automatique, le saut d'un album à l'autre et pour recommencer une playlist
        if (!sharedPrefs.contains("layout") && !sharedPrefs.contains("langue") && !sharedPrefs.contains("defaut")) {
            Toast.makeText(context, "Bienvenue !", Toast.LENGTH_LONG).show();
            activity_main.setDisplayedChild(1);
        }
        getPref();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    /**
     * handling click events for the main menu items
     * @param item
     * @return
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(activity_main.getDisplayedChild() == 1)
        {
            //Ajouter les nouvelle valeur dans les pref
            if(layoutOption.getCheckedRadioButtonId() == R.id.rightOption)
                editor.putBoolean("layout", true);
            else
                editor.putBoolean("layout", false);

            editor.putString("langue", ((RadioButton) langueOption.findViewById(langueOption.getCheckedRadioButtonId())).getText().toString());
            editor.putString("defaut", ((RadioButton) defautOption.findViewById(defautOption.getCheckedRadioButtonId())).getText().toString());
            editor.commit();
            openHome();
        }
        else if(activity_main.getDisplayedChild() != 0)
            openHome();
        else
            super.onBackPressed();
    }

    /**
     * Open the photo mode page
     */
    void openPhoto(){
        Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show();
        //activity_main.setDisplayedChild();
    }

    /**
     * Open Writing page
     */
    void openWriting(){
        Toast.makeText(this, "writing", Toast.LENGTH_SHORT).show();
        //activity_main.setDisplayedChild();
    }

    /**
     * Open Keyboard page
     */
    void openKeyboard(){
        Toast.makeText(this, "keyboard", Toast.LENGTH_SHORT).show();
        //activity_main.setDisplayedChild();
    }

    /**
     * open parameter page
     */
    void openParameter(){
        activity_main.setDisplayedChild(1); //the parameter page is 1
    }

    /**
     * open home page
     */
    void openHome() {
        activity_main.setDisplayedChild(0); //the home page is 0
    }


    /**
     * Adjust the text size depending on the screen size
     * @param height the height of the screen
     */
    void adjustTextToScreen(int height){
        int textViewSize = height/45;
        int radioBtnTxtSize = height/65;
        layoutOptionsText.setTextSize(textViewSize);
        languageOptionsText.setTextSize(textViewSize);
        defaultModeOptionsText.setTextSize(textViewSize);

        for(int i = 0; i < layoutOption.getChildCount(); i++)
            ((RadioButton) layoutOption.getChildAt(i)).setTextSize(radioBtnTxtSize);

        for(int i = 0; i < langueOption.getChildCount(); i++)
            ((RadioButton) langueOption.getChildAt(i)).setTextSize(radioBtnTxtSize);

        for(int i = 0; i < defautOption.getChildCount(); i++)
            ((RadioButton) defautOption.getChildAt(i)).setTextSize(radioBtnTxtSize);
    }


    /**
     * Lire les preférence du user
     */
    private void getPref() {
        //set les variables de préférences
        isDroitier = sharedPrefs.getBoolean("layout", true);
        ((RadioButton) layoutOption.getChildAt(0)).setChecked(isDroitier);
        ((RadioButton) layoutOption.getChildAt(1)).setChecked(!isDroitier);

        langue = sharedPrefs.getString("langue", getResources().getString(R.string.francais));

        for(int i = 0; i < langueOption.getChildCount(); i++) {
            if (((RadioButton) langueOption.getChildAt(i)).getText().equals(langue))
                ((RadioButton) langueOption.getChildAt(i)).setChecked(true);
            else
                ((RadioButton) langueOption.getChildAt(i)).setChecked(false);
        }


        defautMode = sharedPrefs.getString("defaut", getResources().getString(R.string.accueil));
        for(int i = 0; i < defautOption.getChildCount(); i++) {
            if (((RadioButton) defautOption.getChildAt(i)).getText().equals(defautMode))
                ((RadioButton) defautOption.getChildAt(i)).setChecked(true);
            else
                ((RadioButton) defautOption.getChildAt(i)).setChecked(false);
        }
    }

    public void confirmPref(View view) {
        onBackPressed();
    }
}
