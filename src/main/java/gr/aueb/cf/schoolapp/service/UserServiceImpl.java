package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.dao.IUserDAO;
import gr.aueb.cf.schoolapp.dto.UserCredentialsDTO;
import gr.aueb.cf.schoolapp.model.User;
import gr.aueb.cf.schoolapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.schoolapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.schoolapp.service.util.JPAHelper;
import gr.aueb.cf.schoolapp.service.util.LoggerUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Provider
@RequestScoped
public class UserServiceImpl implements IUserService{
    @Inject
    IUserDAO userDAO;

    @Override
    public User insertUser(UserCredentialsDTO userDTO) throws EntityAlreadyExistsException {
        User user;
        try {
            JPAHelper.beginTransaction();
            user = map(userDTO);

            if (userDTO.getId() == null){
                userDAO.insert(user);
            }else {
                throw new EntityAlreadyExistsException(User.class, user.getId());
            }

            JPAHelper.commitTransaction();
        }catch (EntityAlreadyExistsException e){
            JPAHelper.rollbackTransaction();
            LoggerUtil.getCurrentLogger().warning("Insert User - " +
                    " rollback - entity already exists");
            throw e;
        }finally {
            JPAHelper.closeEntityManager();
        }
        return user;
    }

    @Override
    public User updateUser(UserCredentialsDTO dto) throws EntityNotFoundException {
        User userToUpdate;

        try {
            JPAHelper.beginTransaction();
            userToUpdate = map(dto);

            if (userDAO.getById(userToUpdate.getId()) == null){
                throw new EntityNotFoundException(User.class, userToUpdate.getId());
            }
            userDAO.update(userToUpdate);
            JPAHelper.commitTransaction();
        }catch (EntityNotFoundException e){
            JPAHelper.commitTransaction();
            LoggerUtil.getCurrentLogger().warning("Update rollback - Entity not found");
            throw e;
        }finally {
            JPAHelper.closeEntityManager();
        }
        return userToUpdate;
    }

    @Override
    public void deleteUser(Long id) throws EntityNotFoundException {
        try {
            JPAHelper.beginTransaction();
            if (userDAO.getById(id) == null){
                throw new EntityNotFoundException(User.class, id);
            }
            userDAO.delete(id);
            JPAHelper.commitTransaction();
        }catch (EntityNotFoundException e){
            JPAHelper.rollbackTransaction();
            LoggerUtil.getCurrentLogger().warning("Delete rollback - Entity not found");
        }finally {
            JPAHelper.closeEntityManager();
        }

    }

    @Override
    public List<User> getUserByUsername(String username) throws EntityNotFoundException {
        List<User> users;

        try {
            JPAHelper.beginTransaction();

            users = userDAO.getByUsername(username);

            if (users.size() == 0){
                throw new EntityNotFoundException(User.class, 0L);
            }
            JPAHelper.commitTransaction();
        }catch (EntityNotFoundException e){
            JPAHelper.rollbackTransaction();
            LoggerUtil.getCurrentLogger().warning("Get user rollback " +
                    "- User not found");
            throw e;
        }finally {
            JPAHelper.closeEntityManager();
        }
        return users;
    }

    @Override
    public User getUserById(Long id) throws EntityNotFoundException {
        User user;
        try {
            JPAHelper.beginTransaction();
            user = userDAO.getById(id);

            if (user == null){
                throw new EntityNotFoundException(User.class, id);
            }
            JPAHelper.commitTransaction();
        }catch (EntityNotFoundException e){
            JPAHelper.rollbackTransaction();
            LoggerUtil.getCurrentLogger().warning("Get user by id rollback " +
                    " -user not found");
            throw e;
        }finally {
            JPAHelper.closeEntityManager();
        }
        return user;
    }

    private User map(UserCredentialsDTO dto){
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }
}
