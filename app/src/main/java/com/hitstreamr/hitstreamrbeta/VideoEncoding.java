package com.hitstreamr.hitstreamrbeta;
import android.os.Bundle;
import android.util.Log;

import com.bitmovin.api.BitmovinApi;
import com.bitmovin.api.encoding.AclEntry;
import com.bitmovin.api.encoding.AclPermission;
import com.bitmovin.api.encoding.EncodingOutput;
import com.bitmovin.api.encoding.InputStream;
import com.bitmovin.api.encoding.codecConfigurations.AACAudioConfig;
import com.bitmovin.api.encoding.codecConfigurations.CodecConfig;
import com.bitmovin.api.encoding.codecConfigurations.H264VideoConfiguration;
import com.bitmovin.api.encoding.codecConfigurations.enums.ProfileH264;
import com.bitmovin.api.encoding.encodings.Encoding;
import com.bitmovin.api.encoding.encodings.StartEncodingRequest;
import com.bitmovin.api.encoding.encodings.muxing.FMP4Muxing;
import com.bitmovin.api.encoding.encodings.muxing.MP4Muxing;
import com.bitmovin.api.encoding.encodings.muxing.MuxingStream;
import com.bitmovin.api.encoding.encodings.muxing.TSMuxing;
import com.bitmovin.api.encoding.encodings.streams.Stream;
import com.bitmovin.api.encoding.enums.CloudRegion;
import com.bitmovin.api.encoding.enums.StreamSelectionMode;
import com.bitmovin.api.encoding.inputs.GcsInput;
import com.bitmovin.api.encoding.inputs.HttpsInput;
import com.bitmovin.api.encoding.inputs.InputType;
import com.bitmovin.api.encoding.manifest.dash.DashManifest;
import com.bitmovin.api.encoding.manifest.hls.HlsManifest;
import com.bitmovin.api.encoding.outputs.Output;
import com.bitmovin.api.encoding.outputs.S3Output;
import com.bitmovin.api.encoding.status.Task;
import com.bitmovin.api.enums.Status;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.*;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VideoEncoding {
    //private static String ekey = "62483f27-c8fb-462b-96dc-3a69510750ba";
    private static BitmovinApi bitmovin;
    private static final String INPUT_PATH = "https://storage.googleapis.com/hitstreamr-beta.appspot.com/videos/ULmaCgF3wkcFEyonDKzmNVLov0C2/testing.mp4";
    private static final String OUTPUT_PATH = "https://storage.googleapis.com/hitstreamr-beta.appspot.com/videos/ULmaCgF3wkcFEyonDKzmNVLov0C2";
    private static final String S3_BUCKET_NAME = "hitstreamr-beta.appspot.com";
    private static final String S3_ACCESS_KEY = "GOOGH7FAZZWE2DKVDO23DNVO";
    private static final String S3_SECRET_KEY = "LFUQvyDjfF/FOtg8bmXhESlDAwuJy9lQwrQCoUfI";
    private static Encoding encoding;


    protected void onCreate(Bundle savedInstanceState) {
        try {
            System.out.println("inside the try block ");
            bitmovin = new BitmovinApi("62483f27-c8fb-462b-96dc-3a69510750ba");
            System.out.println("after bitmovin ");
            // start();
        }
        catch(Exception e)
        {
            System.out.println("inside the catch block ");
            e.printStackTrace();
            //Log.e("IO","IO"+e);
            System.out.println("Exception..... "+e);
        }
    }

    // public void onCreate(Bundle savedInstanceState) throws IOException, BitmovinApiException, UnirestException, URISyntaxException, RestException, InterruptedException {
    public static void main(String[] args) throws IOException, BitmovinApiException, UnirestException, URISyntaxException, RestException, InterruptedException, NullPointerException, ExecutionException, InvocationTargetException {

        //System.out.println(Unirest.get("http://www.google.com").asStringAsync().get().getBody());
        try {
            System.out.println("before GCS.... ");

            GcsInput Ginput = new GcsInput();

            Ginput.setBucketName(S3_BUCKET_NAME);
            Ginput.setSecretKey(S3_SECRET_KEY);
            Ginput.setAccessKey(S3_ACCESS_KEY);
            Ginput = bitmovin.input.gcs.create(Ginput);

            System.out.println("before setting output..... ");
            //start();
            S3Output s3Output = new S3Output();
            s3Output.setBucketName(S3_BUCKET_NAME);
            s3Output.setSecretKey(S3_SECRET_KEY);
            s3Output.setAccessKey(S3_ACCESS_KEY);
            s3Output = bitmovin.output.s3.create(s3Output);

            System.out.println("before calling the video config..... ");
            H264VideoConfiguration videoCodecConfig = new H264VideoConfiguration();

            videoCodecConfig.setName("my-h264-4-8mbit-1080p-cc");
            videoCodecConfig.setBitrate(4800000L);
            videoCodecConfig.setHeight(1080);
            videoCodecConfig.setProfile(ProfileH264.HIGH);

            videoCodecConfig = bitmovin.configuration.videoH264.create(videoCodecConfig);

            Encoding encoding = new Encoding();

            encoding.setName("my-awesome-first-encoding");
            encoding.setCloudRegion(CloudRegion.GOOGLE_US_EAST_1);

            encoding = bitmovin.encoding.create(encoding);

            Stream videoStream = createStream(Ginput, videoCodecConfig);
            videoStream = bitmovin.encoding.stream.addStream(encoding, videoStream);

            FMP4Muxing fmp4VideoMuxing = addFmp4VideoMuxing(encoding, s3Output, videoStream);

            bitmovin.encoding.start(encoding);

        }
        catch(Exception e)
        { System.out.println("Exception..... "+e);
        }

    }


    private static Stream createStream(GcsInput input, CodecConfig codecConfig)
    {

        System.out.println("inside the create stream block ");
        Stream stream = new Stream();

        InputStream inputStream = new InputStream();
        inputStream.setInputId(input.getId());
        inputStream.setInputPath(INPUT_PATH);
        inputStream.setSelectionMode(StreamSelectionMode.AUTO);
        stream.setCodecConfigId(codecConfig.getId());
        stream.addInputStream(inputStream);

        return stream;
    }


    private static FMP4Muxing addFmp4VideoMuxing(Encoding encoding, Output output, Stream videoStream) throws URISyntaxException, BitmovinApiException, RestException, UnirestException, IOException
    {
        return addFmp4Muxing(encoding, output, videoStream, "/video/1080p/fmp4/");
    }

    private static FMP4Muxing addFmp4Muxing(Encoding encoding, Output output, Stream stream, String path) throws URISyntaxException, BitmovinApiException, RestException, UnirestException, IOException
    {
        FMP4Muxing fmp4Muxing = new FMP4Muxing();

        MuxingStream muxingSream = new MuxingStream();
        muxingSream.setStreamId(stream.getId());

        AclEntry aclEntry = new AclEntry();
        aclEntry.setPermission(AclPermission.PUBLIC_READ);
        List<AclEntry> aclEntries = new ArrayList<AclEntry>();
        aclEntries.add(aclEntry);

        EncodingOutput encodingOutput = new EncodingOutput();
        encodingOutput.setOutputId(output.getId());
        encodingOutput.setOutputPath(String.format("%s%s", OUTPUT_PATH, path));
        encodingOutput.setAcl(aclEntries);

        fmp4Muxing.setSegmentLength(4D);
        fmp4Muxing.setSegmentNaming("seg_%number%.m4s");
        fmp4Muxing.setInitSegmentName("init.mp4");
        fmp4Muxing.addStream(muxingSream);
        fmp4Muxing.addOutput(encodingOutput);

        return bitmovin.encoding.muxing.addFmp4MuxingToEncoding(encoding, fmp4Muxing);
    }

    private static TSMuxing addTsVideoMuxing(Encoding encoding, Output output, Stream videoStream) throws URISyntaxException, BitmovinApiException, RestException, UnirestException, IOException
    {
        return addTsMuxing(encoding, output, videoStream, "/video/1080p/ts/");
    }

    private static TSMuxing addTsMuxing(Encoding encoding, Output output, Stream stream, String path) throws URISyntaxException, BitmovinApiException, RestException, UnirestException, IOException
    {
        TSMuxing tsMuxing = new TSMuxing();

        MuxingStream muxingStream = new MuxingStream();
        muxingStream.setStreamId(stream.getId());

        AclEntry aclEntry = new AclEntry();
        aclEntry.setPermission(AclPermission.PUBLIC_READ);
        List<AclEntry> aclEntries = new ArrayList<AclEntry>();
        aclEntries.add(aclEntry);

        EncodingOutput encodingOutput = new EncodingOutput();
        encodingOutput.setOutputId(output.getId());
        encodingOutput.setOutputPath(String.format("%s%s", OUTPUT_PATH, path));
        encodingOutput.setAcl(aclEntries);

        tsMuxing.setSegmentLength(4D);
        tsMuxing.setSegmentNaming("seg_%number%.ts");
        tsMuxing.addStream(muxingStream);
        tsMuxing.addOutput(encodingOutput);

        return bitmovin.encoding.muxing.addTSMuxingToEncoding(encoding, tsMuxing);
    }




}
