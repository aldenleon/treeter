package com.aldenleon.treeter.repository;

import com.aldenleon.treeter.model.Comment;
import com.aldenleon.treeter.projection.CommentProjection;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    /**
     * @param page zero-indexed paged access
     */
    @Transactional
    @NativeQuery("""
            WITH RECURSIVE search_tree(id, text_content, parent_id, up, dw, level, path) AS (
                    SELECT id, text_content, parent_id, up, dw, 1 as level, ARRAY[0.0]
                    FROM comment WHERE id = :rootId
                UNION ALL
                    SELECT c.id, c.text_content, c.parent_id, c.up, c.dw, level + 1, path ||
                        (1 - ((c.up + 0.821187208) / (c.up + c.dw) - 1.281551565545 *
                        SQRT(((c.up * c.dw) / (c.up + c.dw) + 0.410593604 / (c.up +
                        c.dw)) / (c.up + c.dw))) / (1 + 1.642374415 / (c.up + c.dw)))
                    FROM comment c, search_tree st
                    WHERE c.parent_id = st.id and level + 1 < :maxDepth
            )
            SELECT id, text_content, parent_id, up, dw
            FROM search_tree ORDER BY path
            LIMIT :pageSize OFFSET :pageSize * :page;
            """)
    List<CommentProjection> findTreeById(Long rootId, int maxDepth, int pageSize, int page);

    @Transactional
    @NativeQuery("""
            insert into comment (text_content, parent_id, up, dw)
            values (:textContent, :parentId, :up, :dw)
            returning id, text_content, parent_id, up, dw;
            """)
    CommentProjection save(Long parentId, String textContent, int up, int dw);

    @Transactional
    @NativeQuery("""
            delete from comment where id = :id
            and (select count(*) from comment where parent_id = :id) = 0
            returning id, text_content, parent_id, up, dw;
            """)
    CommentProjection delete(Long id);

    @Transactional
    @NativeQuery("""
            update comment
            set text_content = :textContent
            where id = :id
            returning id, text_content, parent_id, up, dw;
            """)
    CommentProjection update(Long id, String textContent);
}
