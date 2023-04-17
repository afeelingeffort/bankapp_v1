package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tenco.bank.repository.model.User;

@Mapper // MyBatis 의존 설정함
public interface UserRepository {

	public int insert(User user); 

	public int updateById(User user); 

	public int deleteById(Integer id); // id 기반 삭제

	public User findById(Integer id); // 한 사람 조회

	public List<User> findAll(); // 모두 조회

}