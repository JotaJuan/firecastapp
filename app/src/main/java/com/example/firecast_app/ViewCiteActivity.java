package com.example.firecast_app;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class ViewCiteActivity extends AppCompatActivity {

    public static final String RANKING_KEY = "ranking";
    public static final String COUNT_RANKING_KEY = "count";
    public static final String TAG = "InspiringQuote";

    private boolean flag= true;//La bandera se utiliza para cortar loop
    private String idCite;

    TextView quoteView;
    TextView authorView;
    TextView rankingView;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cite);

        quoteView = (TextView) findViewById(R.id.textViewQuote);
        authorView = (TextView) findViewById(R.id.textViewAuthor);
        rankingView = (TextView) findViewById(R.id.textViewRanking);
        ratingBar = findViewById(R.id.ratingBar);

        Bundle bundle = getIntent().getExtras();

        idCite = bundle.getString("id");
        quoteView.setText(bundle.getString("quote"));
        authorView.setText(bundle.getString("author"));
        rankingView.setText("Rating: " +
                calcRating(bundle.getString("ranking"), bundle.getString("count")));
    }

    /**
     * Se leen los campos ranking y count de la cita para que "updateScore" operé con ellos y posteriormente
     * se presente al usuario el ranting real de la cita.
     * @param view
     */
    public void score (View view) {

        CollectionReference db = FirebaseFirestore.getInstance().collection("sampleData");
        DocumentReference doc = db.document(idCite);

        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists() && flag){//Flag es una variable para evitar un loop infinito
                    String rating = documentSnapshot.getString(RANKING_KEY);
                    String count = documentSnapshot.getString(COUNT_RANKING_KEY);

                    flag = false;//se utiliza para que naturaleza de "addSnapshotListener" no provoque un loop infinito
                    updateScore(rating,  count);
                    Toast.makeText(getApplicationContext(), "You score was saved!", Toast.LENGTH_SHORT).show();
                } else if (e != null){
                    Log.w(TAG, "Got an exception!", e);
                }
            }
        });
    }

    /**
     * Actualiza los parametros Ranking y count de la base de datos.
     * Notesé: 1- que Ranking y rating se usan como sinonimos aunque semanticamente puede haber diferencias
     * 2- El campo Ranking no representa la valoración de la cita real sino una sumatoria de calificaciones,
     * con el que junto con el campo count se calcula la valoración
     * @param rating
     * @param count
     */
    public void updateScore (String rating, String count) {

        float valueSumRating = Float.parseFloat(rating);
        float valuecount = Float.parseFloat(count);
        float valueRating = this.ratingBar.getRating();
        float valueFinal;

        valuecount++;
        valueFinal = (valueSumRating + valueRating);

        CollectionReference db = FirebaseFirestore.getInstance().collection("sampleData");
        DocumentReference doc = db.document(idCite);

        doc.update(RANKING_KEY, Float.toString(valueFinal));
        doc.update(COUNT_RANKING_KEY, Float.toString(valuecount));
    }

    /**
     * Calcula la valoración de la cita el cual es un promedio de la sumatoria de las calificaciones
     * sobre el contador de veces votado al iniciar el activity
     * @param sumRating
     * @param count
     * @return
     */
    private String calcRating(String sumRating, String count){

        float valueSumRating = Float.parseFloat(sumRating);
        float valuecount = Float.parseFloat(count);
        float valueNewRating;
        String strRating;

        DecimalFormat df = new DecimalFormat("##.#");
        df.setRoundingMode(RoundingMode.DOWN);

        if (valueSumRating > 0 && valuecount > 0){
            valueNewRating = valueSumRating / valuecount;
            df.format(valueNewRating);
            strRating = df.format(valueNewRating);
        }else{
            strRating = "Nueva";
        }

        return strRating;
    }

    public void back (View view){
        finish();
    }
}
