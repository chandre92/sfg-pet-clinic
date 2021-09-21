package guru.springframework.sfgpetclinic.controller;

import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;

@Controller
@RequestMapping("/owners")
public class OwnerController {
    public static final String CREATE_OR_UPDATE_OWNER_FORM = "owner/createOrUpdateOwnerForm";

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @RequestMapping("/find")
    public String findOwners(Model model) {
        model.addAttribute("owner", Owner.builder().build());

        return "owner/findOwners";
    }

    @GetMapping
    public String processFindForm(Owner owner, BindingResult bindingResult, Model model) {
        // allow parameterless GET request for /owners to return all records
        if (owner.getLastName() == null) {
            owner.setLastName(""); // empty string signifies the broadest possible search
        }

        // find owners by last name
        Collection<Owner> searchResult = ownerService.findAllByLastName(owner.getLastName());
        if (searchResult.isEmpty()) {
            // no owners found
            bindingResult.rejectValue("lastName", "notFound", "not found");
            return "owner/findOwners";
        } else if (searchResult.size() == 1) {
            // 1 owner found
            owner = searchResult.iterator().next();
            return "redirect:/owners/" + owner.getId();
        } else {
            // multiple results found
            model.addAttribute("selections", searchResult);
            return "owner/ownersList";
        }
    }

    @GetMapping("/{ownerId}")
    public ModelAndView showOwner(@PathVariable("ownerId") Long ownerId) {
        ModelAndView modelAndView = new ModelAndView("owner/ownerDetails");
        modelAndView.addObject(ownerService.findById(ownerId));

        return modelAndView;
    }

    @GetMapping("/new")
    public String initCreateForm(Model model) {
        model.addAttribute("owner", new Owner());
        return CREATE_OR_UPDATE_OWNER_FORM;
    }

    @PostMapping("/new")
    public String processCreateForm(@Validated Owner owner, BindingResult bindingResult) {
        owner.setLastName("foobar");
        if (bindingResult.hasErrors()) {
            return CREATE_OR_UPDATE_OWNER_FORM;
        } else {
            Owner savedOwner = ownerService.save(owner);
            return "redirect:/owners/" + savedOwner.getId();
        }
    }

    @GetMapping("/{ownerId}/edit")
    public String initUpdateForm(@PathVariable("ownerId") Long ownerId, Model model) {
        model.addAttribute("owner", ownerService.findById(ownerId));
        return CREATE_OR_UPDATE_OWNER_FORM;
    }

    @PostMapping("/{ownerId}/edit")
    public String processUpdateForm(@Validated Owner owner,
                                    BindingResult bindingResult,
                                    @PathVariable("ownerId") Long ownerId) {
        if (bindingResult.hasErrors()) {
            return CREATE_OR_UPDATE_OWNER_FORM;
        } else {
            owner.setId(ownerId);
            Owner savedOwner = ownerService.save(owner);
            return "redirect:/owners/" + savedOwner.getId();
        }
    }
}
