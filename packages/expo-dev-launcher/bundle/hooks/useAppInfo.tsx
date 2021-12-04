import * as React from 'react';

import { AppInfo } from '../native-modules/DevLauncherInternal';

const Context = React.createContext<AppInfo | null>(null);
export const useAppInfo = () => React.useContext(Context);

type AppInfoProviderProps = {
  children: React.ReactNode;
  initialAppInfo?: AppInfo;
};

export function AppInfoProvider({ children, initialAppInfo }: AppInfoProviderProps) {
  return <Context.Provider value={initialAppInfo}>{children}</Context.Provider>;
}
