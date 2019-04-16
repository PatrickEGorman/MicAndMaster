#ifndef MICANDMASTER_AUDIOPROCESSOR_H
#define MICANDMASTER_AUDIOPROCESSOR_H

#include <jni.h>
#include <oboe/Oboe.h>
#include <string>
#include <vector>


class WaveSampleData
{
private:
    std::string path;
public:
    WaveSampleData(std::string path);
    std::vector<int> GetSampleAmps();
};




#endif //MICANDMASTER_AUDIOPROCESSOR_H
