package aj.ajay.quizapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    int[] images = new int[] {
            R.drawable.general,
            R.drawable.science,
            R.drawable.enter,
            R.drawable.history,
            R.drawable.sports,
            R.drawable.world
    };

    String[] text = new String[] {
            "General",
            "Science",
            "Entertainment",
            "History",
            "Sports",
            "World"
    };

    private GridView gridView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gridView = (GridView) findViewById(R.id.grid_view);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6494979048790657/2038225113");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        String u = auth.getUid();
        assert u != null;
        if (u != null) {
            databaseReference.child(u).child("Username")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String s = (String) dataSnapshot.getValue();
                            getSupportActionBar().setTitle(s);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createDialog(position);
            }
        });
    }

    private void createDialog(final int p){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_start,null,false);
        Button materialButton = (Button) view.findViewById(R.id.start_quiz);
        final CheckBox checkBox1 = (CheckBox) view.findViewById(R.id.one);
        final CheckBox checkBox2 = (CheckBox) view.findViewById(R.id.two);
        final CheckBox checkBox3 = (CheckBox) view.findViewById(R.id.three);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        checkBox1.setChecked(true);
        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);
            }
        });
        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox1.setChecked(false);
                checkBox3.setChecked(false);
            }
        });
        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox2.setChecked(false);
                checkBox1.setChecked(false);
            }
        });
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send = text[p].toLowerCase();
                int n = 0;
                if (checkBox1.isChecked()) {
                    n = 10;
                }else if (checkBox2.isChecked()) {
                    n = 20;
                }else if (checkBox3.isChecked()) {
                    n = 30;
                }else {
                    n = 10;
                }
                Intent intent = new Intent(ContentActivity.this,QuizActivity.class);
                intent.putExtra("category",send);
                intent.putExtra("number",n);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null){
            Intent intent = new Intent(ContentActivity.this,Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            auth.signOut();
            Intent intent = new Intent(ContentActivity.this,Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_change_username) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.custom_input,null,false);
            final EditText e = view.findViewById(R.id.edit_username);
            Button b = view.findViewById(R.id.change);
            builder.setView(view);
            final AlertDialog dialog = builder.create();
            dialog.show();
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newUsername = e.getText().toString();
                    if (newUsername.isEmpty()){
                        Toast.makeText(ContentActivity.this, "Field should not be empty", Toast.LENGTH_SHORT).show();
                    }else {
                        String s = auth.getUid();
                        assert s != null;
                        databaseReference.child(s).child("Username").setValue(newUsername);
                        dialog.dismiss();
                        getSupportActionBar().setTitle(newUsername);
                    }
                }
            });
        }
        if (id == R.id.action_show) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }else {
                startActivity(new Intent(ContentActivity.this, ShowDetails.class));
            }
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    startActivity(new Intent(ContentActivity.this, ShowDetails.class));
                }
            });
        }
        if (id == R.id.action_privacy_policy) {
            try {
                Uri uri = Uri.parse("https://sites.google.com/view/online-quiz");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, this.getPackageName());
                this.startActivity(intent);
            }catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class GridViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.custom_grid_view,null,false);
            CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.custom_image);
            TextView textView = (TextView) view.findViewById(R.id.custom_text);
            circleImageView.setImageResource(images[position]);
            textView.setText(text[position]);
            return view;
        }
    }
}
