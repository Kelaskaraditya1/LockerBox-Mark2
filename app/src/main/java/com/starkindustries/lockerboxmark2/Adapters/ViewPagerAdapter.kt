package com.starkindustries.lockerboxmark2.Adapters
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.starkindustries.lockerboxmark2.Fragments.DocumentsListFragment
import com.starkindustries.lockerboxmark2.Fragments.HomeFragment
import com.starkindustries.lockerboxmark2.Fragments.ImagesListFragment
import com.starkindustries.lockerboxmark2.Fragments.MusicListFragment
import com.starkindustries.lockerboxmark2.Fragments.ProfileFragment
import com.starkindustries.lockerboxmark2.Fragments.UploadFragment
import com.starkindustries.lockerboxmark2.Fragments.VideosListFragment

class ViewPagerAdapte(var context:Context,manager:FragmentManager):FragmentPagerAdapter(manager)
{
    override fun getCount(): Int
    {
        return 4
    }

    override fun getItem(position: Int): Fragment
    {
        lateinit var fragment:Fragment
        when(position)
        {
            0->
                fragment = ImagesListFragment()
            1->
                fragment = DocumentsListFragment()
            2->
                fragment = MusicListFragment()
            3->
                fragment = VideosListFragment()
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        lateinit var title:String
        when(position)
        {
            0-> title="Images"
            1->title="Documents"
            2->title="Musics"
            3->title="Videos"
        }
        return title
    }
}