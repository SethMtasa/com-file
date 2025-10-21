//package prac.lease.email;
//
//
//import lombok.RequiredArgsConstructor;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import prac.lease.dto.LeaseResponseDto;
//import prac.lease.model.User;
//import prac.lease.repository.LeaseRepository;
//import prac.lease.repository.UserRepository;
//import zw.co.netone.genericemailsender.AdvancedEmailService.SimpleMailService;
//
//import java.util.Optional;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class NotificationService {
//
//    @Value("${spring.mail.username}")
//    private String sourceEmail;
//    private final SimpleMailService mailService;
//    private final UserRepository userRepository;
//    private final LeaseRepository leaseRepository;
//
//
//    @Async
//    public void sendNewLeaseNotification(String userName, LeaseResponseDto responseDto){
//        Optional<User> user = userRepository.findByUsername(userName);
//        mailService.setEmail(
//                sourceEmail,
//                "estates@example.com"
//                ,user.get().getFirstName() +" "+ user.get().getLastName() +" , has added a new lease under site name "+ responseDto.getSite() +" and landlord " + responseDto.getLandlord() +". \n\n "+ "Regards \n " + "Lease Management System")
//                .subject(" Addition Of New Lease")
//                .send();
//
//
//    }
//
//}
