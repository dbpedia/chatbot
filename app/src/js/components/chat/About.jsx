import React from 'react';
import Modal from 'react-modal';

class About extends React.Component {
    getUrlParam(name) {
        return decodeURIComponent(window.location.search.replace(new RegExp("^(?:.*[&\\?]" + encodeURIComponent(name).replace(/[\.\+\*]/g, "\\$&") + "(?:\\=([^&]*))?)?.*$", "i"), "$1"));
    }

    constructor(props) {
        super(props);

        this.onCloseClick = this.onCloseClick.bind(this);
    }

    onCloseClick(event) {
        event.preventDefault();
        if(this.props.hide) {
            this.props.hide();
        }
    }

    render() {
        return (
            <div>
                <Modal
                    isOpen={this.props.isOpen}
                    contentLabel="About Modal"
                    style={{content: {border: 'none', background: 'none', top: 20, left: 20, right: 20, bottom: 20, zIndex: 10}}}>

                    <div className="card feedback-container slideDown">
                        <form>
                            <fieldset>
                                <legend>About</legend>

                                <h5>DBpedia Projects</h5>
                                <ol>
                                    <li>AKSW Genesis</li>
                                    <li>QANARY (WD Aqua)</li>
                                    <li>DBpedia Lookup</li>
                                    <li>DBpedia Spotlight</li>
                                </ol>

                                <h5>Chat Libraries</h5>
                                <ol>
                                    <li>RiveScript</li>
                                    <li>Eliza</li>
                                </ol>

                                <h5>Platform Libraries</h5>
                                <ol>
                                    <li>Messenger4j</li>
                                    <li>JSlack</li>
                                </ol>

                                <h5>External APIs</h5>
                                <ol>
                                    <li>Wolfram Alpha</li>
                                    <li>Nominatim API</li>
                                    <li>TMDB API</li>
                                </ol>

                                <h5>General Libraries</h5>
                                <ol>
                                    <li>Apache Jena</li>
                                    <li>JLanguage Tool</li>
                                    <li>Jackson</li>
                                    <li>GSON</li>
                                    <li>jUnit</li>
                                    <li>Google jUnit Toolbox</li>
                                </ol>

                                <h5>Front Libraries/Frameworks</h5>
                                <ol>
                                    <li>React.js</li>
                                    <li>Twitter Bootstrap</li>
                                    <li>Bootstrap Material Design</li>
                                    <li>LESS</li>
                                </ol>
                                <h5>Backend Libraries/Frameworks</h5>
                                <ol>
                                    <li>Spring</li>
                                    <li>Cloudant</li>
                                </ol>

                                <div className="form-group">
                                    <button onClick={this.onCloseClick} className="btn btn-raised btn-primary">Close</button>
                                </div>
                            </fieldset>

                        </form>
                    </div>
                </Modal>
            </div>
        )
    }
}

About.defaultProps = {

};
export default About;