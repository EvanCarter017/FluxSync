import service from "@/services/request";

const useToken = () => {

    const refresh = async (refresh_token: string): Promise<string> => {
        return await service.post("/api/security/jwt/refresh", {refresh_token}).then(result => {
            return result.data.access_token;
        }).catch(e => {
            if (e.status === 401) return -2;
            return -1;
        });
    };

    return {
        refresh
    }

}

export default useToken;