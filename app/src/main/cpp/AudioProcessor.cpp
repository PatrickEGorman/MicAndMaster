#include <vector>
#include <string>
#include <oboe/Oboe.h>
#include "AudioProcessor.h"


class MyCallback : public oboe::AudioStreamCallback {
public:
    oboe::DataCallbackResult
    onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) override {

        // We requested AudioFormat::Float so we assume we got it. For production code always check what format
        // the stream has and cast to the appropriate type.
        auto *outputData = static_cast<int16_t *>(audioData);

        return oboe::DataCallbackResult::Continue;
    }
};

WaveSampleData::WaveSampleData(std::string path):path(path) {

    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Output);
    builder.setFormat(oboe::AudioFormat::Float);
    builder.setChannelCount(oboe::ChannelCount::Stereo);
    MyCallback myCallback;
    builder.setCallback(&myCallback);

    oboe::AudioStream *stream;
    oboe::Result result = builder.openStream(&stream);
    stream->requestStart();

}
std::vector<int> WaveSampleData::GetSampleAmps()
{
    std::vector<int> list;
    for (int i=0; i<5; i++){
        list.push_back(i);
    }
    return list;
}

