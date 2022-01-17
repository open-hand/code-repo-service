import { useLocalStore } from 'mobx-react-lite';

interface RefObject<T> {
  readonly current: T | null;
}

export default function useStore(defaultValue:string) {
  return useLocalStore(() => ({
    selectedTabkey: defaultValue || 'psSet',
    setSelectedTab(value:string){
      this.selectedTabkey = value;
    }
  }));
}

export type MainStoreProps = ReturnType<typeof useStore>;
