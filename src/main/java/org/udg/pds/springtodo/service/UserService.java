package org.udg.pds.springtodo.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.configuration.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Recepta;
import org.udg.pds.springtodo.entity.Task;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.ReceptaRepository;
import org.udg.pds.springtodo.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReceptaService receptaService;

    @Autowired
    private ReceptaRepository receptaRepository;

    public User matchPassword(String username, String password) {

        List<User> uc = userRepository.findByUsername(username);

        if (uc.size() == 0) throw new ServiceException("User does not exists");

        User u = uc.get(0);

        if (u.getPassword().equals(password))
            return u;
        else
            throw new ServiceException("Password does not match");
    }

    public User register(String username, String email, String password, String descripcio) {

        List<User> uEmail = userRepository.findByEmail(email);
        if (uEmail.size() > 0)
            throw new ServiceException("Email already exist");


        List<User> uUsername = userRepository.findByUsername(username);
        if (uUsername.size() > 0)
            throw new ServiceException("Username already exists");

        User nu = new User(username, email, password, descripcio);
        userRepository.save(nu);
        return nu;
    }

    public User getUser(Long id) {
        Optional<User> uo = userRepository.findById(id);
        if (uo.isPresent())
            return uo.get();
        else
            throw new ServiceException(String.format("User with id = % dos not exists", id));
    }

    public User getUserProfile(long id) {
        User u = this.getUser(id);
        for (Task t : u.getTasks())
            t.getTags();
        return u;
    }

    public void deleteUser(Long userId) {
        User u = this.getUser(userId);
        userRepository.delete(u);
    }

    public UserRepository crud(){
        return userRepository;
    }

    public User updateUser(Long id, String nom, String email, String descripcio){
        User user = this.getUser(id);
        user.setUsername(nom);
        user.setEmail(email);
        user.setDescripcio(descripcio);

        userRepository.save(user);

        return user;
    }

    @Transactional
    public void addReceptaPreferits(Long userId, Long receptaId){
        User u = getUser(userId);
        Recepta r = receptaService.getRecepta(receptaId);
        Boolean aux = u.getReceptesPreferides().contains(r);
        if(!aux) u.addReceptaPreferida(r);
        userRepository.save(u);
    }

    public void removeReceptaPreferits(Long userId, Long productId)
    {
        User u = getUser(userId);
        Recepta recepta = receptaRepository.findById(productId).get();
        Boolean aux = u.getReceptesPreferides().contains(recepta);
        if (aux) u.removeReceptaPreferida(recepta);
        userRepository.save(u);
    }
}
