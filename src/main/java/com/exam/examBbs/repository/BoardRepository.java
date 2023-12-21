package com.exam.examBbs.repository;

import com.exam.examBbs.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {



    /*검색기능 추가*/

}
