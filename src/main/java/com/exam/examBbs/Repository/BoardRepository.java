package com.exam.examBbs.Repository;

import com.exam.examBbs.Domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {

}
