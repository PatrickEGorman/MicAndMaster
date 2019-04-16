#include <vector>
#include <string>
#include <oboe/Oboe.h>
#include "AudioProcessor.h"

WaveSampleData::WaveSampleData(std::string path):path(path) {}
std::vector<int> WaveSampleData::GetSampleAmps()
{
    std::vector<int> list;
    for (int i=0; i<5; i++){
        list.push_back(i);
    }
    return list;
}