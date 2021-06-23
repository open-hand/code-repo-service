import { useLocalStore } from 'mobx-react-lite';
import { ReactElement } from 'react';

interface RefObject<T> {
  readonly current: T | null;
}

export default function useStore() {
  return useLocalStore(() => ({
    selectedTabkey: 'psSet',
    setSelectedTab(value:string){
      console.log(value);
      this.selectedTabkey = value;
    }
  }));
}

export type MainStoreProps = ReturnType<typeof useStore>;
