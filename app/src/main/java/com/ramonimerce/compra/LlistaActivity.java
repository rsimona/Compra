package com.ramonimerce.compra;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LlistaActivity extends AppCompatActivity {
    public static DataBaseHelper mDbHelper = null;
    ListView llista;
    TaskAdapter items;
    AutoCompleteTextView entrada;
    EditText quantitat;
    ImageView afegir;
    private SlidingUpPanelLayout mLayout;
    private LinearLayout up;
    private LinearLayout down;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llista);

        // Definim el color de la ActionBar
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffFF8800));
        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon((R.mipmap.ic_launcher));


        llista = (ListView) findViewById(R.id.llista);
        entrada = (AutoCompleteTextView) findViewById(R.id.entrada);
        entrada.clearFocus();

        // Obrim la base de dades
        mDbHelper = new DataBaseHelper(this);
        try {
            fillData();
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(R.string.dataError);
        }

        afegir = (ImageView) findViewById(R.id.afegir);
        afegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quantitat = (EditText) findViewById(R.id.quantitat);
                String article = entrada.getText().toString();

                int quantArticles = 1;
                if (!quantitat.getText().toString().equals("")) {
                    quantArticles = Integer.parseInt(quantitat.getText().toString());
                }


                if ((article.equals("")) || (quantitat.getText().toString().equals(""))) {

                    Toast.makeText(getApplicationContext(), R.string.entradaBuida, Toast.LENGTH_LONG).show();

                } else {

                    DataBaseHelper db = new DataBaseHelper(getApplicationContext());
                    db.open();
                    db.insertItem(article, quantArticles, false);
                    db.close();

                    try {
                        fillData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                }

                entrada.setText("");
                quantitat.setText("1");


            }
        });

        Button mes = (Button) findViewById(R.id.mes);
        mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantitat = (EditText) findViewById(R.id.quantitat);

                if (!quantitat.getText().toString().equals("")) {
                    int quantArticles = Integer.parseInt(quantitat.getText().toString());
                    quantArticles++;

                    if (quantArticles <= 0) {
                        quantitat.setText("0");
                    } else {
                        quantitat.setText(Integer.toString(quantArticles));
                    }
                } else {
                    quantitat.setText("1");
                }

            }
        });

        Button menys = (Button) findViewById(R.id.menys);
        menys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantitat = (EditText) findViewById(R.id.quantitat);

                if (!quantitat.getText().toString().equals("")) {
                    int quantArticles = Integer.parseInt(quantitat.getText().toString());
                    quantArticles--;

                    if (quantArticles <= 0) {
                        quantitat.setText("0");
                    } else {
                        quantitat.setText(Integer.toString(quantArticles));
                    }
                } else {
                    quantitat.setText("1");
                }
            }
        });

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        up = (LinearLayout) findViewById(R.id.up);
        down = (LinearLayout) findViewById(R.id.down);
        mLayout.setPanelSlideListener(new PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                up.setVisibility(View.VISIBLE);
                down.setVisibility(View.GONE);
            }

            @Override
            public void onPanelExpanded(View view) {
                up.setVisibility(View.GONE);
                down.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_llista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillData() throws SQLException {

        // s'obre la base de dades i se n'obtenen els elements
        mDbHelper.open();
        Cursor itemCursor = mDbHelper.getItems();
        ListEntry item = null;
        ArrayList<ListEntry> resultList = new ArrayList<ListEntry>();

        // processem el resultat
        while (itemCursor.moveToNext()) {

            String article = itemCursor.getString(itemCursor.getColumnIndex(DataBaseHelper.ARTICLE));
            int quantitat = itemCursor.getInt(itemCursor.getColumnIndex(DataBaseHelper.QUANTITAT));
            boolean comprat = (itemCursor.getInt(itemCursor.getColumnIndex(DataBaseHelper.COMPRAT))) != 0;

            item = new ListEntry();

            item.article = article;
            item.quantitat = quantitat;
            item.comprat = comprat;
            resultList.add(item);
        }

        // tanquem el cursor i la base de dades
        itemCursor.close();
        mDbHelper.close();

        // generem l'adaptador
        items = new TaskAdapter(this, R.layout.row_list, resultList, getLayoutInflater());
        if (items.getCount() == 0) {
            TextView textView = (TextView) findViewById(R.id.buit);
            textView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = (TextView) findViewById(R.id.buit);
            textView.setVisibility(View.GONE);
        }
        llista.setAdapter(items);
    }

    private void showMessage(int message) {
        Context context = getApplicationContext();
        CharSequence text = getResources().getString(message);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }


    private class ListEntry {
        int id;
        String article;
        int quantitat;
        boolean comprat;
    }

    private class TaskAdapter extends ArrayAdapter<ListEntry> {
        private LayoutInflater mInflater;
        private List<ListEntry> mObjects;

        public TaskAdapter(Context context, int resource, List<ListEntry> objects, LayoutInflater mInflater) {
            super(context, resource, objects);
            this.mInflater = mInflater;
            this.mObjects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            // si es null el creem
            if (row == null) {
                // obtención de la vista de la línea de la tabla
                row = mInflater.inflate(R.layout.row_list, null, false);
                ViewHolder holder = new ViewHolder();
                holder.comprat = (CheckBox) row.findViewById(R.id.chbxComprat);
                holder.article = (TextView) row.findViewById(R.id.row_article);
                holder.quantitat = (TextView) row.findViewById(R.id.row_quantitat);
                row.setTag(holder);
            }

            ListEntry listEntry = mObjects.get(position);

            // omplim les dades
            ViewHolder holder = (ViewHolder) row.getTag();
            holder.article.setText(listEntry.article);
            holder.quantitat.setText(String.valueOf(listEntry.quantitat));

            // depenent de si està comprat o no marquem el CheckBox
            holder.comprat.setTag(listEntry.comprat);

            if (listEntry.comprat) {
                holder.comprat.setChecked(true);
            } else {
                holder.comprat.setChecked(false);
            }

            return row;
        }
    }

    static class ViewHolder {
        CheckBox comprat;
        TextView article;
        TextView quantitat;
    }
}
