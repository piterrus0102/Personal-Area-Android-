package ru.elesta.jupiter_lkselfguard.HomeFragment


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentInfo
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory

class HomeFragmentContainer: Fragment() {

    lateinit var homeFragmentContainer: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v("HomeFragmentContainer", "onCreateView")
        Log.v("---", "-----------------------------------------")
        val v = inflater.inflate(R.layout.home_fragment_container_of_object, null)
        homeFragmentContainer = v
        val viewPager = v.findViewById<ViewPager>(R.id.myViewPager)
        var adapter: FragmentPagerAdapter
        var parent: String? = null
        val bundle = this.arguments
        if (bundle != null) {
            parent = bundle.getString("objectFragment")
        }
        if (parent == "common") {
            adapter = MyAdapterCommon(childFragmentManager)
            viewPager.adapter = adapter
        }
        if (parent == "selfguard") {
            adapter = MyAdapterSelfguard(childFragmentManager)
            viewPager.adapter = adapter
        }
        viewPager.setCurrentItem(0)
        return v
    }

    inner class MyAdapterCommon(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    HomeFragmentSections()
                }
                else -> HomeFragmentSections()
            }
        }

        override fun getCount(): Int {
            return 1
        }

    }

    inner class MyAdapterSelfguard(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    HomeFragmentSections()
                }
                1 -> {
                    HomeFragmentInfo()
                }
                else -> HomeFragmentSections()
            }
        }

        override fun getCount(): Int {
            return 2
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v("HomeFragmentContainer", "onDestroyView")
        Log.v("---", "-----------------------------------------")
    }

    override fun onPause() {
        super.onPause()
        Log.v("HomeFragmentContainer", "onPause")
        Log.v("---", "-----------------------------------------")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v("HomeFragmentContainer", "onAttach")
        Log.v("---", "-----------------------------------------")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v("HomeFragmentContainer", "onActivityCreated")
        Log.v("---", "-----------------------------------------")
    }

    override fun onStart() {
        super.onStart()
        Log.v("HomeFragmentContainer", "onStart")
        Log.v("---", "-----------------------------------------")
    }

    override fun onResume() {
        super.onResume()
        Log.v("HomeFragmentContainer", "onResume")
        Log.v("---", "-----------------------------------------")
    }


    override fun onStop() {
        super.onStop()
        Log.v("HomeFragmentContainer", "onStop")
        Log.v("---", "-----------------------------------------")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("HomeFragmentContainer", "onDestroy")
        Log.v("---", "-----------------------------------------")
    }

    override fun onDetach() {
        super.onDetach()
        Log.v("HomeFragmentContainer", "onDetach")
        Log.v("---", "-----------------------------------------")
    }
}