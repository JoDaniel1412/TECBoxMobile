package com.example.tecboxmobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONArray


class StorageActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "SERVER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.storage, menu)

        val navTitle: TextView = findViewById(R.id.nav_header_title)
        val navSubTitle: TextView = findViewById(R.id.nav_header_subtitle)
        navTitle.text = mAuth.currentUser?.displayName ?: ""
        navSubTitle.text = mAuth.currentUser?.email ?: ""
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        requestStorage()
    }


    fun signOut(item: MenuItem) {
        mAuth.signOut()

        // Switch to Log in activity if log in success
        val i = Intent(applicationContext, MainActivity::class.java)
        startActivity(i)
    }

    fun settings(item: MenuItem) {
        // Switch to settings activity if log in success
        val i = Intent(applicationContext, SettingsActivity::class.java)
        startActivity(i)
    }
    
    private fun requestStorage() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

        // Set http url
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val ip = pref.getString("ip", "localhost")
        val port = pref.getString("port", "8080")
        val url = "http://$ip:$port/api/package"

        // Instantiate string request
        val jsonObjectRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                fillPackageView(response)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, error.toString())
            }
        )

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

    @SuppressLint("SetTextI18n")
    private fun fillPackageView(json: JSONArray) {
        val packageView : TableLayout = findViewById(R.id.package_view)
        for (i in 0 until json.length()) {
            val pack = json.getJSONObject(i)
            val routeId = pack.getString("RouteId")
            val trackingId = pack.getString("TrackingId")
            val billId = pack.getString("BillId")
            val state = pack.getString("State")

            // Instantiate columns views
            val row = TableRow(this)
            val textID = TextView(this)
            val textTracking = TextView(this)
            val textBill = TextView(this)
            val textState = TextView(this)

            // Text value assignation
            textID.text = routeId
            textTracking.text = trackingId
            textBill.text = billId
            textState.text = state

            // Added columns to hierarchy
            row.addView(textID)
            row.addView(textTracking)
            row.addView(textBill)
            row.addView(textState)
            packageView.addView(row)
        }
    }
}
