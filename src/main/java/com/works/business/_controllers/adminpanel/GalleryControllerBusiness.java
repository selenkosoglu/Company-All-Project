package com.works.business._controllers.adminpanel;

import com.works.entities.Gallery;
import com.works.entities.images.GalleryImage;
import com.works.properties.GalleryImageInterlayer;
import com.works.repositories._jpa.GalleryImageRepository;
import com.works.repositories._jpa.GalleryRepository;
import com.works.utils.Util;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class GalleryControllerBusiness {
    final String rvalue = "adminpanel/gallery/";
    final GalleryRepository galleryRepository;
    final GalleryImageRepository galleryImageRepository;

    public GalleryControllerBusiness(GalleryRepository galleryRepository, GalleryImageRepository galleryImageRepository) {
        this.galleryRepository = galleryRepository;
        this.galleryImageRepository = galleryImageRepository;
    }

    public String galleryAddShow(Model model) {
        model.addAttribute("gallery", new Gallery());
        return rvalue + "galleryadd";
    }

    public String galleryList(Model model) {
        model.addAttribute("ls", galleryRepository.getGalleryInfo(Util.theCompany.getId()));
        return rvalue + "gallerylist";
    }

    public String galleryAdd(Gallery gallery, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            galleryRepository.save(gallery);
        } else {
            System.err.println(Util.errors(bindingResult));
        }
        return rvalue + "galleryadd";
    }

    public String galleryUpdateShow(String stIndex, Model model) {
        try {
            Integer index = Integer.parseInt(stIndex);
            Optional<Gallery> optionalGallery = galleryRepository.findById(index);
            if (optionalGallery.isPresent()) {
                Gallery gallery = optionalGallery.get();
                model.addAttribute("gallery", gallery);
                model.addAttribute("index", index);
                return rvalue + "galleryupdate";
            } else {
                System.err.println("Goruntulenmek istenen galeri mevcut değil");
                return "error/404";
            }
        } catch (Exception ex) {
            System.err.println("Url kismina String deger girilmis");
            return "error/404";
        }
    }

    public String galleryUpdate(Gallery gallery, BindingResult bindingResult, String stIndex, Model model) {
        Integer index = 0;
        try {
            index = Integer.parseInt(stIndex);
        } catch (Exception ex) {
            return "error/404";
        }
        Optional<Gallery> optionalGallery = galleryRepository.findById(index);
        if (!bindingResult.hasErrors()) {
            if (optionalGallery.isPresent()) {
                Gallery tempGallery = optionalGallery.get();
                tempGallery.setGallery_title(gallery.getGallery_title());
                tempGallery.setGallery_detail(gallery.getGallery_detail());
                tempGallery.setGallery_status(gallery.getGallery_status());
                galleryRepository.saveAndFlush(tempGallery);
                return "redirect:/admin/gallery/list";
            } else {
                System.err.println("Kullanıcı formdaki id yi değiştirmiştir. \n" +
                        "Bu id ye sahip galeri mevcut değildir.");
                return "error/404";
            }
        } else {
            System.err.println(Util.errors(bindingResult));
            model.addAttribute("index", index);
        }
        return rvalue + "galleryupdate";
    }

    Integer gallery_index = 0;

    public String galleryImagesShow(String stIndex, Model model) {
        try {
            gallery_index = Integer.parseInt(stIndex);
            Optional<Gallery> optGallery = galleryRepository.findById(gallery_index);
            if (optGallery.isPresent()) {
                //    List<GalleryImage> galleryImageList = galleryImageRepository.findByGallery_IdEquals(index);
                model.addAttribute("ls", optGallery.get().getGalleryImages());
                model.addAttribute("index", gallery_index);
                return rvalue + "galleryimagelist";
            } else {
                System.err.println("Mudahale edilmis. Girilen id de bir galeri yok");
                return "redirect:/admin/gallery/list";
            }
        } catch (Exception ex) {
            System.err.println("Mudahale edilmis. String ifade girilmis");
            return "redirect:/admin/gallery/list";
        }
    }

    public String galleryImageAddShow(Model model) {
        model.addAttribute("ls", galleryRepository.findByCompany_IdEquals(Util.theCompany.getId()));
        model.addAttribute("galleryImageInterlayer", new GalleryImageInterlayer());
        model.addAttribute("isError", 0);
        return rvalue + "galleryimageadd";
    }

    public String galleryImageUpdateShow(String stIndex, Model model) {
        try {
            Integer index = Integer.parseInt(stIndex);
            Optional<GalleryImage> optionalGalleryImage = galleryImageRepository.findById(index);
            if (optionalGalleryImage.isPresent()) {
                GalleryImage galleryImage = optionalGalleryImage.get();
                model.addAttribute("galleryImage", galleryImage);
                model.addAttribute("index", index);
                return rvalue + "galleryimageupdate";
            } else {
                System.err.println("Düzenlenmek istenen resim mevcut değil");
                return "error/404";
            }
        } catch (Exception ex) {
            System.err.println("Url kismina String deger girilmis");
            return "error/404";
        }
    }

    public String galleryImageUpdate(GalleryImage galleryImage, BindingResult bindingResult, String stIndex, Model model) {
        Integer index = 0;
        try {
            index = Integer.parseInt(stIndex);
        } catch (Exception ex) {
            return "error/404";
        }
        Optional<GalleryImage> optionalGalleryImage = galleryImageRepository.findById(index);
        if (!bindingResult.hasErrors()) {
            if (optionalGalleryImage.isPresent()) {
                GalleryImage tempGalleryImage = optionalGalleryImage.get();
                tempGalleryImage.setGallery_image_title(galleryImage.getGallery_image_title());
                galleryImageRepository.saveAndFlush(tempGalleryImage);
                return "redirect:/admin/gallery/images/" + gallery_index;
            } else {
                System.err.println("Kullanıcı formdaki id yi değiştirmiştir. \n" +
                        "Bu id ye sahip galeri mevcut değildir.");
                return "error/404";
            }
        } else {
            System.err.println(Util.errors(bindingResult));
            model.addAttribute("index", index);
        }
        return rvalue + "galleryimageupdate";
    }

    public String galleryImageAdd(GalleryImageInterlayer galleryImageInterlayer, BindingResult bindingResult, Model model) {
        System.out.println(bindingResult);
        if (!bindingResult.hasErrors()) {
            Optional<Gallery> optionalGallery = galleryRepository.findById(galleryImageInterlayer.getGallery_title());
            GalleryImage galleryImage = new GalleryImage();
            galleryImage.setGallery(optionalGallery.get());
            galleryImage.setGallery_image_title(galleryImageInterlayer.getGallery_image_title());

            String fileName = StringUtils.cleanPath(galleryImageInterlayer.getGallery_image_files().getOriginalFilename());
            String ext = "";
            try {
                int length = fileName.lastIndexOf(".");
                ext = fileName.substring(length, fileName.length());
            } catch (Exception e) {
                model.addAttribute("ls", galleryRepository.findByCompany_IdEquals(Util.theCompany.getId()));
                model.addAttribute("galleryImageInterlayer", new GalleryImageInterlayer());
                model.addAttribute("isError", 1);
                return rvalue + "galleryimageadd";
            }
            String uui = UUID.randomUUID().toString();
            fileName = uui + ext;
            galleryImage.setGallery_image_url(fileName);
            try {
                galleryImageRepository.save(galleryImage);

                File theDir = new File(Util.UPLOAD_DIR + "gallery/" + optionalGallery.get().getId());
                if (!theDir.exists()) {
                    theDir.mkdirs();
                }
                Path path = Paths.get(Util.UPLOAD_DIR + "gallery/" + optionalGallery.get().getId() + "/" + fileName);
                Files.copy(galleryImageInterlayer.getGallery_image_files().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                model.addAttribute("ls", galleryRepository.findByCompany_IdEquals(Util.theCompany.getId()));
                model.addAttribute("isError", 0);
                return rvalue + "galleryimageadd";
            }
            return "redirect:/admin/gallery/image";
        } else {
            System.err.println(Util.errors(bindingResult));
            model.addAttribute("ls", galleryRepository.findByCompany_IdEquals(Util.theCompany.getId()));
            model.addAttribute("isError", 0);
            return rvalue + "galleryimageadd";
        }
    }


}
