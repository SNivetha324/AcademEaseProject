package com.ac.maduidesigns


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView



class MainActivitynavigation : AppCompatActivity() {

   // private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       Thread.sleep(2000)
       installSplashScreen()
        setContentView(R.layout.activity_mainnavigation)

    val bottomnav = findViewById<BottomNavigationView>(R.id.bottom_nav)

       loadFragment(homeFragment(), supportFragmentManager)



        bottomnav.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.home -> {
                    loadFragment(homeFragment(),  supportFragmentManager)
                    true
                }
//                R.id.search -> {
//                    replaceFragment(searchFragment())
//                    true
//                }
                R.id.notif -> {
                    loadFragment(notificFragment(), supportFragmentManager)
                    true
                }
                R.id.profile -> {
                    loadFragment(profileFragment(), supportFragmentManager)
                    true
                }
                else -> false

            }

        }
       //replaceFragment(homeFragment())
    }
    private fun loadFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()

//    private fun replaceFragment(fragment: Fragment){
//        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
//    }*/
    }
}

