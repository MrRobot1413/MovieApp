package ru.mrrobot1413.movieapp.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.databinding.FragmentDetailsBinding
import ru.mrrobot1413.movieapp.interfaces.MovieClickListener
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.ui.MainActivity
import ru.mrrobot1413.movieapp.viewModels.FavoriteListViewModel
import ru.mrrobot1413.movieapp.viewModels.MoviesViewModel
import java.text.SimpleDateFormat
import java.util.*

class DetailsFragment : Fragment() {

    private lateinit var inviteText: String
    private val favoriteListViewModel by lazy {
        ViewModelProvider(this).get(FavoriteListViewModel::class.java)
    }
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    lateinit var binding: FragmentDetailsBinding
    private var isAddedToFavorite = false
    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        (activity as MovieClickListener).hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.viewModel = moviesViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        moviesViewModel.movieDetailed.observe(viewLifecycleOwner, { movie ->
            setImage(binding.imageBackdrop, ("https://image.tmdb.org/t/p/w342" + movie.posterPath))

            setOnFabClickListener(movie)

            favoriteListViewModel.selectById(movie.id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    isAddedToFavorite =
                        if (result != null) {
                            if (result.liked) {
                                setIconLiked()
                                true
                            } else {
                                setIconUnliked()
                                false
                            }
                        } else {
                            setIconUnliked()
                            false
                        }
                }, {})


            inviteText = getString(R.string.invite_text) + " " + movie.title
        })

        arguments?.getInt(MainActivity.MOVIE)?.let {
            moviesViewModel.getMovieDetails(it,
                getString(R.string.no_connection),
                getString(R.string.error_loading_movies))
        }

        (activity as MainActivity?)!!.setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setOnFabClickListener(movie: Movie?) {
        binding.fabAddToFavorite.setOnClickListener {
            if (movie != null) {
                if (isAddedToFavorite) {
                    isAddedToFavorite = false

                    movie.liked = false
                    favoriteListViewModel.delete(movie)

                    setIconUnliked()
                } else {
                    isAddedToFavorite = true

                    movie.liked = true
                    movie.time = moviesViewModel.movieDetailed.value?.time!!
                    favoriteListViewModel.insert(movie)

                    setIconLiked()
                }
            }
        }
    }

    private fun setImage(view: ImageView, url: String) {
        context?.let {
            Glide.with(it)
                .load(url)
                .transform(CenterCrop())
                .error(ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_error_24))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        binding.progress.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        binding.progress.visibility = View.GONE
                        return false
                    }

                })
                .into(view)
        }
    }

    private fun setIconLiked() {
        binding.fabAddToFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_favorite
            )
        )
    }

    private fun setIconUnliked() {
        binding.fabAddToFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_favorite_border
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        binding.toolbar.inflateMenu(R.menu.invite_menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.invite_friend) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, binding.collapsingToolbarLayout.title)
            sendIntent.putExtra(Intent.EXTRA_TEXT, inviteText)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        } else if (item.itemId == R.id.watch_later) {
            calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getDefault()
            val datePickerDialog = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText(getString(R.string.select_date))
                .build()

            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText(getString(R.string.select_time))
                .build()

            activity?.supportFragmentManager?.let {
                datePickerDialog.addOnPositiveButtonClickListener { _ ->
                    timePicker.show(it, "timePicker")
                }
                timePicker.addOnPositiveButtonClickListener {
                    calendar.timeInMillis = datePickerDialog.selection!!
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    calendar.set(Calendar.MINUTE, timePicker.minute)
                    calendar.set(Calendar.SECOND, 0)
                    moviesViewModel.scheduleNotification(moviesViewModel.movieDetailed.value?.title!!,
                        calendar,
                        requireContext(),
                        moviesViewModel.movieDetailed.value!!.id)

//                    val format = SimpleDateFormat("HH:mm dd MMM, yyyy")
//                    val formatted = format.format(calendar.time)
//
//                    moviesViewModel.movieDetailed.value!!.reminder = formatted
//                    moviesViewModel.movieDetailed.value!!.isToNotify = true
//                    favoriteListViewModel.insert(moviesViewModel.movieDetailed.value!!)
                }
                datePickerDialog.show(it, "datePicker")
            }
        }
//        else if(item.itemId == R.id.watch_later_cancel){
//            moviesViewModel.unscheduleNotification(moviesViewModel.movieDetailed.value!!, MoviesViewModel.WORK_TAG, requireContext())
//            favoriteListViewModel.updateDatabaseRecord(moviesViewModel.movieDetailed.value!!)
//            activity?.onBackPressed()
//        }
        return super.onOptionsItemSelected(item)
    }
}