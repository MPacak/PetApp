package hr.pet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hr.pet.adapter.DogAdapter
import hr.pet.databinding.FragmentDogBinding
import hr.pet.framework.fetchDogs
import hr.pet.model.Dog


class DogFragment : Fragment() {
    private lateinit var binding: FragmentDogBinding
    private lateinit var dogs: MutableList<Dog>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dogs = requireContext().fetchDogs()
        binding = FragmentDogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = DogAdapter(requireContext(), dogs)
        }
    }
}