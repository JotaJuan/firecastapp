package com.example.firecast_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import javax.annotation.Nullable;


public class ListQuoteActivity extends AppCompatActivity {

    public static final String QUOTE_KEY = "quote";
    public static final String AUTHOR_KEY ="author";
    public static final String RANKING_KEY = "ranking";
    public static final String COUNT_RANKING_KEY = "count";
    public static final String TAG = "InspiringQuote";

    ListView listViewQuotes;//Despliega la lista en la pantalla
    ArrayList<String> listQuotes;//Array de String que representan las citas
    ArrayAdapter<String> adapter;

    @Override
    protected void onStart() {
        super.onStart();

        CollectionReference db = FirebaseFirestore.getInstance().collection("sampleData");
        //Se extrae de la base de datos todos las citas
        db
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String quoteText = document.getString(QUOTE_KEY);
                                if (notRepeat(quoteText, listQuotes.size())){
                                    listQuotes.add(quoteText);//Se agregan las citas a la lista
                                    adapter.notifyDataSetChanged();//Notifica los cambios realizados
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_quote);
        //Incializacion de variables
        listViewQuotes = (ListView) findViewById(R.id.listViewCites);
        listQuotes = new ArrayList<>();

        adapter = new ArrayAdapter<String> (this,android.R.layout
                .simple_expandable_list_item_1, listQuotes);
        listViewQuotes.setAdapter(adapter);

        //Se captura el click sobre la lista
        listViewQuotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                findQuote(listQuotes.get(position));
            }
        });
    }

    /**
     * Se realiza la busqueda de la cita y se presenta al usuario en un nuevo activity junto con el
     * autor y su valoración.
     * @param cite
     */
    private void findQuote(String cite){

        CollectionReference db = FirebaseFirestore.getInstance().collection("sampleData");

        db
                .whereEqualTo("quote", cite)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("author") != null) {

                                String idCite = doc.getId();
                                String varQuote = doc.getString(QUOTE_KEY);
                                String varAuthor = doc.getString(AUTHOR_KEY);
                                String varRanking = doc.getString(RANKING_KEY);
                                String varCount = doc.getString(COUNT_RANKING_KEY);

                                Intent i = new Intent(ListQuoteActivity.this, ViewCiteActivity.class);
                                i.putExtra("id",idCite);
                                i.putExtra("quote",varQuote);
                                i.putExtra("author",varAuthor);
                                i.putExtra("ranking",varRanking);
                                i.putExtra("count",varCount);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                finish();
                            }
                        }
                    }
                });
    }

    public void back(View view){
        finish();
    }

    /**
     * Metodo auxiliar que verifica que el la cita no este repetida
     * @param quote
     * @param sizeList
     * @return
     */
    private boolean notRepeat(String quote, int sizeList){
        boolean notrepeated = true;

        for (int i = 0; i < sizeList ; ++i) {//poner en funcion
            if (quote.equals(listQuotes.get(i))){
                notrepeated = false;
            }
        }

        return notrepeated;
    }
}
