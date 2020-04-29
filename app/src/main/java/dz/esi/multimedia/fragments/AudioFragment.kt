package dz.esi.multimedia.fragments


import android.os.Bundle
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment

import dz.esi.multimedia.R


/**
 * A simple [Fragment] subclass.
 */
class AudioFragment : Fragment() {

    private var mp: MediaPlayer? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_audio, container, false)
        val btn = rootView.findViewById<Button>(R.id.btnPlayAudio)
        btn.setOnClickListener { lireAudio(R.raw.idir) }
        return rootView
    }



    fun lireAudio(resId: Int) {
        if (mp == null) {        //set up MediaPlayer
            mp = MediaPlayer.create(activity, resId)

            try {
                mp!!.prepare()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!mp!!.isPlaying())
            mp!!.start()
        else
            mp!!.pause()
    }

}// Required empty public constructor
