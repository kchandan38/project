package com.gendbservices.dbservices.configuration;

public interface DatabaseServiceConstants {

    String INSERT_POOL_DATA = "Insert into falcon.generic_db_service(uuid, pool_request_object, creation_time) value(?, ? ,?)";
    String GET_POOL_DATA = "Select uuid, pool_request_object from falcon.generic_db_service";
    String ENCRYPTION_KEY = "GenDatabaseService64BitW348324%&^%^%#^%!$#$*(`FS$YS*&HJJ*HK*&JHK*JHD$A%$#@!*&";
    String UNICODE_FORMAT = "UTF-8";
    String SHA_ONE = "SHA-1";
    String ENCRYPTION_ALGO = "AES";
    String TRANSFORMATION_TYPE = "AES/ECB/PKCS5PADDING";
    int NO_OF_BIT_ENC = 16;
}
