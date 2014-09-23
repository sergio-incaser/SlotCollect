package es.incaser.apps.slotcollect;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.text.ChoiceFormat;


public class EstablecimientosLogic extends ListActivity {
    static ResultSet resultSet= null;
    static SQLConnection sqlConnection = SQLConnection.getInstance();
    static EstabLogicAdapter estabLogicAdapter;
    static int rowCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_establecimientos_logic);
        estabLogicAdapter = new EstabLogicAdapter(this);
        new getData().execute(1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.establecimientos_logic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class getData extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            resultSet = sqlConnection.getEstablecimientos();
            try {
                if (resultSet.last()){
                    rowCount = resultSet.getRow();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                rowCount = 0;
            }

            return "Datos importados";
        }
        @Override
        protected void onPostExecute(String result){
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            if (resultSet != null) {
                setListAdapter(estabLogicAdapter);
            }
        }
    }

    private static int getRowCount(ResultSet resultSet) {
        if (resultSet == null) {
            return 0;
        }
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
        return 0;
    }
    public static class EstabLogicAdapter extends BaseAdapter{
        private Context myContext;

        public EstabLogicAdapter (Context ctx){
            myContext = ctx;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int i) {
            try {
                //return resultSet.getRowId(i);
                resultSet.absolute(i);
                return  resultSet.getRow();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            // TODO Auto-generated method stub
            View myView = null;

            RowId currentRow = null;
            if (convertView == null) {
                LayoutInflater myInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myView = myInflater.inflate(R.layout.item_establecimiento_logic, null);
            } else {
                myView = convertView;
            }

            try {
                resultSet.absolute(i);
                //currentRow = resultSet.getRowId(i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            TextView txtCodigo = (TextView) myView.findViewById(R.id.codigoLogic);
            TextView txtItem = (TextView) myView.findViewById(R.id.itemNameLogic);

            try {
                txtCodigo.setText(resultSet.getString(resultSet.findColumn("INC_CodigoEstablecimiento")));
                txtItem.setText(resultSet.getString(resultSet.findColumn("RazonSocial")));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return myView;
        }
    }

}
