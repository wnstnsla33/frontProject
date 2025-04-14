package pProject.pPro.friends;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pProject.pPro.ServiceUtils;
import pProject.pPro.entity.FriendsEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.friends.DTO.FriendsListDTO;
import pProject.pPro.friends.DTO.FriendsRequestDTO;
import pProject.pPro.friends.DTO.RequestFriendsType;
import pProject.pPro.friends.exception.FriendsErrorCode;
import pProject.pPro.friends.exception.FriendsException;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendsService {

	private final FriendsRepository friendsRepository;
	private final ServiceUtils utils;

	public void requestFriends(String email, Long FriendsId) {
		UserEntity me = utils.findUser(email);
		int requestCount = friendsRepository.findRequest(me.getUserId(),FriendsId);
		if(requestCount>0)throw new FriendsException(FriendsErrorCode.DUPLICATE_REQUEST);
		UserEntity friend = utils.findUserById(FriendsId);
		
		FriendsEntity friends = new FriendsEntity(me,friend);
		friendsRepository.save(friends);
	}
	public void updateF(String email,Long FId,RequestFriendsType type) {
		UserEntity user = utils.findUser(email);
		FriendsEntity friends= utils.findFriendsEntity(FId);
		//내아이디에 친추가 맞는지 확인
		if(!friends.getFriend().getUserId().equals( user.getUserId()))
			throw new FriendsException(FriendsErrorCode.NOT_FOUND_FRIENDS_ID);
		int isFriend = friendsRepository.findRequest(user.getUserId(), friends.getFriend().getUserId());
		//친추 되어있는 상태인지 확인
		int requiredCount = friendsRepository.findRequest(user.getUserId(), friends.getFriend().getUserId());
		if(requiredCount>=5)throw new FriendsException(FriendsErrorCode.TOO_MANY_REQUIRE);
		if(isFriend>0)throw new FriendsException(FriendsErrorCode.DUPLICATE_REQUEST);
		//이미 처리한 요청인지 확인
		if(friends.getType()!=RequestFriendsType.REQUEST)
			throw new FriendsException(FriendsErrorCode.ALREADY_REPONSE);
		friends.setType(type);
	}
	public Page<FriendsRequestDTO> findRequestList(String email, Pageable pageable){
		UserEntity user = utils.findUser(email);
	    Page<FriendsEntity> requests = friendsRepository.requestList(user.getUserId(), pageable);
	    return requests.map(FriendsRequestDTO::new);
	}
	public List<FriendsListDTO> findFriendsList(String email){
		UserEntity user = utils.findUser(email);
		List<FriendsEntity> requests = friendsRepository.friendsList(user.getUserId());
	    return requests.stream().map(FriendsListDTO::new)
	                   .collect(Collectors.toList()); 
	}
	public int getRequestCount(String email) {
		UserEntity user = utils.findUser(email);
		return friendsRepository.requestFriendsCount(user.getUserId());
	}
	
}
