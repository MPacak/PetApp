package hr.pet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hr.pet.R
import hr.pet.adapter.DogAdapter
import hr.pet.adapter.OrganizationAdapter
import hr.pet.databinding.FragmentDogBinding
import hr.pet.databinding.FragmentOrgBinding
import hr.pet.framework.fetchDogs
import hr.pet.framework.fetchOrganizations
import hr.pet.model.Dog
import hr.pet.model.Organization


class OrgFragment : Fragment() {
    private lateinit var binding: FragmentOrgBinding
    private lateinit var orgs: MutableList<Organization>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        orgs = requireContext().fetchOrganizations()
        binding = FragmentOrgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvOrgs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = OrganizationAdapter(requireContext(), orgs)
        }
    }
}