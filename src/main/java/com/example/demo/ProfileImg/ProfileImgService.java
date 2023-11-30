package com.example.demo.ProfileImg;

import com.example.demo.User.SiteUser;
import com.example.demo.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImgService {

    private final ProfileImgRepository profileImgRepository;

    @Value("${imgFile.dir}")
    private String fileDir;
    public ProfileImg ProfileImgForCreateUser(SiteUser user, String savedName) {

        ProfileImg profileImg = new ProfileImg();
        profileImg.setUrl(savedName);
        profileImg.setSiteUser(user);

        this.profileImgRepository.save(profileImg);

        return profileImg;
    }

    public String saveFile(MultipartFile files) throws IOException {

        if(files.isEmpty()){
            return null;
        }

        String origName = files.getOriginalFilename();

        String uuid = UUID.randomUUID().toString();

        String extenstion = origName.substring(origName.lastIndexOf("."));

        String savedName = "/Image/" + uuid + extenstion;

        String savedPath = fileDir +savedName;
        files.transferTo(new File(savedPath));

        return savedName;
    }

    public void deletFile(String fileName){

        File file = new File(fileDir + fileName);

        file.delete();

    }

}
