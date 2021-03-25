package edu.vt.cs.cs5254.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.vt.cs.cs5254.criminalintent.databinding.FragmentCrimeBinding
import java.util.UUID


private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"

class CrimeDetailFragment : Fragment() {
    private lateinit var crime: Crime

    private var _binding: FragmentCrimeBinding? = null
    private  val binding get() = _binding!!

    private val viewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        viewModel.loadCrime(crimeId)
//        Log.d(TAG, "Crime fragment created for crime with ID ${viewModel.crime.id}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCrimeBinding.inflate(inflater, container, false)
        val view = binding.root
        updateUI()
        binding.crimeDate.isEnabled = false
        return view
    }

    private fun updateUI() {
        binding.crimeTitle.setText(crime.title)
        binding.crimeDate.text = crime.date.toString()
        binding.crimeSolved.apply{
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            })
    }
    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(sequence: CharSequence?,
                                       start: Int, before: Int, count: Int) {
                crime.title = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) { }
        }
        binding.crimeTitle.addTextChangedListener(titleWatcher)
        binding.crimeSolved.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }
    override fun onStop() {
        super.onStop()
        viewModel.saveCrime(crime)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeDetailFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeDetailFragment().apply {
                arguments = args
            }
        }
    }
}