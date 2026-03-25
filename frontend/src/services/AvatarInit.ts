const avatar = {

};
const avatarIcons = import.meta.glob("@/components/avatars/svg/*/*.vue");

export default function AvatarInit() {
    for (const path in avatarIcons) {
        const name = path.split("/").pop().split(".")[0];
        avatar[name] = avatarIcons[path];
    }
    return avatar;
}