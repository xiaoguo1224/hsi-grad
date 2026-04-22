import request from '@/utils/request';

export function getSpectralList(params) {
    return request({
        url: '/spectral/list',
        method: 'get',
        params
    });
}

export function getAllSpectra() {
    return request({
        url: '/spectral/all',
        method: 'get'
    });
}

export function getSpectralDetail(id) {
    return request({
        url: `/spectral/${id}`,
        method: 'get'
    });
}

export function saveSpectral(data) {
    return request({
        url: '/spectral',
        method: 'post',
        data
    });
}

export function deleteSpectral(id) {
    return request({
        url: `/spectral/${id}`,
        method: 'delete'
    });
}

export function getSimilarity(id) {
    return request({
        url: `/spectral/similarity/${id}`,
        method: 'get'
    });
}

export function getRadarData(id) {
    return request({
        url: `/spectral/radar/${id}`,
        method: 'get'
    });
}
