package dz.esi.multimedia

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import dz.esi.multimedia.fragments.AudioFragment
import dz.esi.multimedia.fragments.CameraFragment
import dz.esi.multimedia.fragments.IntegratedCameraFragment
import dz.esi.multimedia.fragments.VideoFragment

/**
 * Created by Amine on 10/05/2018.
 */
class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return AudioFragment()
            1 -> return VideoFragment()
            2 -> return CameraFragment()
            3 -> return IntegratedCameraFragment()
        }
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return if (position > 4) PlaceholderFragment.newInstance(position + 1) else null
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Audio"
            1 -> return "Vidéo"
            2 -> return "Caméra"
            3 -> return "Caméra intégrée"
        }
        return null
    }
}
