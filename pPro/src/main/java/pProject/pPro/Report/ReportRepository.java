package pProject.pPro.Report;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pProject.pPro.entity.ReportEntity;
import pProject.pPro.entity.UserEntity;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

	@Query("select r from ReportEntity r join fetch r.reporter join fetch r.reportedUser "
			+ "where r.reporter.userId = :reportId")
	Optional<ReportEntity> findReport(@Param("reportId") Long reportId);

	@Query("select r from ReportEntity r join fetch r.reporter rep " +
		       "left join fetch r.reportedUser where (:keyword is null or rep.userNickName like concat('%', :keyword, '%')) " +
		       "and (:status is null or r.status = :status) order by r.createdAt desc")
		Page<ReportEntity> findAllReports(@Param("keyword") String keyword,
		                                  @Param("status") ReportStatus status,
		                                  Pageable pageable);

	@Query("select count(r) > 0 from ReportEntity r " +
		       "where r.reporter = :reporterId and r.targetId = :targetId and r.targetType = :targetType")
		boolean isAlreadyReported(@Param("reporterId") Long id,
		                          @Param("targetId") String targetId,
		                          @Param("targetType") ReportTargetType targetType);
	
	
	List<ReportEntity> findByReporter(UserEntity reporter);
}
