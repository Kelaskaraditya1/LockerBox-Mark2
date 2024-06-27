package com.starkindustries.lockerboxmark2.Adapters
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.starkindustries.lockerboxmark2.Fragments.HomeFragment
import com.starkindustries.lockerboxmark2.Fragments.ProfileFragment
import com.starkindustries.lockerboxmark2.Fragments.UploadFragment
class ViewPagerAdapte(var context:Context,manager:FragmentManager):FragmentPagerAdapter(manager)
{
    override fun getCount(): Int
    {
        return 3
    }

    override fun getItem(position: Int): Fragment
    {
        lateinit var fragment:Fragment
        when(position)
        {
            0->
                fragment = HomeFragment()
            1->
                fragment = UploadFragment()
            2->
                fragment = ProfileFragment()
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        lateinit var title:String
        when(position)
        {
            0-> title="Home"
            1->title="Upload"
            2->title="Profile"
        }
        return title
    }
}