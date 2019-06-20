package com.example.firecast_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public static final String AUTHOR_KEY ="author";
    public static final String QUOTE_KEY = "quote";
    public static final String RANKING_KEY = "ranking";
    public static final String COUNT_RANKING_KEY = "count";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }

    /**
     * Método por el cual una frase o cita se almacena en la base de datos firestore
     * @param view
     */
    public void saveQuote (View view){

        EditText quoteView = (EditText) findViewById(R.id.editTextQuote);
        EditText authorView = (EditText) findViewById(R.id.editTextAuthor);
        String quoteText = quoteView.getText().toString();
        String authorText = authorView.getText().toString();
        String ranking = "0";//incialmente la valoración es 0
        String countRankig = "0";

        CollectionReference db = FirebaseFirestore.getInstance().collection("sampleData");

        if (quoteText.isEmpty() || authorText.isEmpty()) { return;}
        Map<String, Object> dataToSave =  new HashMap<String, Object>();
        dataToSave.put(QUOTE_KEY, quoteText);
        dataToSave.put(AUTHOR_KEY, authorText);
        dataToSave.put(RANKING_KEY, ranking);
        dataToSave.put(COUNT_RANKING_KEY, countRankig);

        db.add(dataToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Document has been saved!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Document was not saved!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Para cargar la lista de citas el usuario presiona el button "load" a partir de ello se genera
     *  un nuevo "activity" que desplega un ListView con las frases encontradas en la db
     * @param view
     */
    public void load (View view){

        Intent i = new Intent (MainActivity.this, ListQuoteActivity.class);
        startActivity(i);

    }

    public void exit(View view){
         finish();
    }

}
