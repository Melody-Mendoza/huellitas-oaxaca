import "./SearchBar.css";

import { Search } from "lucide-react";

function SearchBar({
    value = "",
    onChange,
    disabled = false
}) {
    return (
        <div className="search-container">
            <Search
                size={18}
                className="search-icon"
                aria-hidden="true"
            />

            <input
                className="search-bar"
                type="search"
                placeholder="Buscar por nombre"
                aria-label="Buscar mascota por nombre"
                value={value}
                onChange={onChange}
                disabled={disabled}
            />
        </div>
    );
}

export default SearchBar;
