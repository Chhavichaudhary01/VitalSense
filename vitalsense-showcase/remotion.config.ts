import { Config } from "@remotion/cli/config";

Config.setBrowserExecutable("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
Config.setChromeMode("chrome-for-testing");
Config.setConcurrency(4);
Config.setPixelFormat("yuv420p");
Config.setCodec("h264");
Config.setAudioCodec("aac");
Config.setChromiumOpenGlRenderer("angle");
Config.setCrf(16);
Config.setJpegQuality(100);
Config.setOverwriteOutput(true);
