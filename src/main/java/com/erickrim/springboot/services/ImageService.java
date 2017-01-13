package com.erickrim.springboot.services;

import com.erickrim.springboot.entities.Image;
import com.erickrim.springboot.entities.User;
import com.erickrim.springboot.repositories.ImageRepository;
import com.erickrim.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * ImageService class
 * Created by krime on 1/9/17.
 */
@Service
public class ImageService {

    private static String UPLOAD_ROOT = "upload-dir";

    private final ImageRepository imageRepository;
    private final ResourceLoader resourceLoader;
    private final CounterService counterService;
    private final GaugeService gaugeService;
    private final InMemoryMetricRepository inMemoryMetricRepository;

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;


    @Autowired
    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader,
                        CounterService counterService, GaugeService gaugeService,
                        InMemoryMetricRepository inMemoryMetricRepository,
                        SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {

        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;
        this.counterService = counterService;
        this.gaugeService= gaugeService;
        this.inMemoryMetricRepository = inMemoryMetricRepository;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;

        this.counterService.reset("files.uploaded");
        this.gaugeService.submit("files.uploaded.lastBytes", 0);
        this.inMemoryMetricRepository.set(new Metric<Number>("files.uploaded.totalBytes", 0));
    }

    public Page<Image> findPage(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }

    public Resource findOneImage(String filename) {
        return resourceLoader.getResource("file:"+ UPLOAD_ROOT + "/" + filename);
    }


    public void createImage(MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            imageRepository.save(
                    new Image(
                            file.getOriginalFilename(),
                            userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
            // this below which is supposed to give us he current logged in user is returning null for some reason
            //SecurityContextHolder.getContext().getAuthentication().getName()

            counterService.increment("files.uploaded");
            gaugeService.submit("files.uploaded.lastBytes", file.getSize());
            inMemoryMetricRepository.increment(new Delta<Number>("files.uploaded.totalBytes", file.getSize()));
            messagingTemplate.convertAndSend("/topic/newImage", file.getOriginalFilename());

        }
    }

    @PreAuthorize("@imageRepository.findByName(#filename)?.owner?.username == authentication?.name or hasRole('ADMIN')")
    public void deleteImage(@Param("filename")String filename) throws IOException {

        final Image byName = imageRepository.findByName(filename);
        imageRepository.delete(byName);
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
        messagingTemplate.convertAndSend("/topic/deleteImage", filename);
    }

    // preloading some test data
    @Bean
    CommandLineRunner setUp(ImageRepository imageRepository,
                            UserRepository userRepository) throws IOException {

        return (args) -> {

            // create images
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

            Files.createDirectory(Paths.get(UPLOAD_ROOT));


            // create users
            User eric = userRepository.save(new User("eric", "krim", "ROLE_ADMIN", "ROLE_USER"));
            User rob  = userRepository.save(new User("rob", "winch", "ROLE_USER"));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test"));
            imageRepository.save(new Image("test", eric));

            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test2"));
            imageRepository.save(new Image("test2", rob));

            FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/test3"));
            imageRepository.save(new Image("test3", eric));


        };
    }

    // preloading some test data and
    // adding configuration report
//    @Bean
//    CommandLineRunner setUp(ImageRepository imageRepository, ConditionEvaluationReport report) throws IOException {
//
//        return (args) -> {
//            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
//
//            Files.createDirectory(Paths.get(UPLOAD_ROOT));
//
//            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test"));
//            imageRepository.save(new Image("test"));
//
//            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test2"));
//            imageRepository.save(new Image("test2"));
//
//            FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/test3"));
//            imageRepository.save(new Image("test3"));
//
//            report.getConditionAndOutcomesBySource().entrySet().stream()  // get every entry from the report
//                    .filter(entry -> entry.getValue().isFullMatch()) // filter on the ones that match only
//                    .forEach(entry -> {
//                        System.out.println(entry.getKey() + " => Match? " + entry.getValue().isFullMatch());
//                    });
//        };
//    }

}
